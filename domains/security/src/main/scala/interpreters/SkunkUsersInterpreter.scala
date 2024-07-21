package com.principate.midas.security
package interpreters

import cats.effect.MonadCancelThrow
import cats.effect.Resource
import cats.effect.kernel.Sync
import cats.syntax.all.*
import scribe.Scribe
import skunk.Command
import skunk.Query
import skunk.Session
import skunk.SqlState
import skunk.implicits.sql

import java.util.UUID

import algebras.Users
import algebras.Users.CreateUserError.EmailAlreadyInUse
import instances.codecs.*
import model.*

final class SkunkUsersInterpreter[F[_]: Sync: Scribe](
    sessionPool: Resource[F, Session[F]]
) extends Users[F]:
  import SkunkUsersInterpreter.*

  override def create(
      email: Email,
      firstName: FirstName,
      lastName: LastName,
      passwordHash: PasswordHash
  ): F[Either[Users.CreateUserError, User]] =
    Scribe[F].trace(
      s"Creating user: { email: $email, firstName: $firstName, lastName: $lastName }"
    ) *> sessionPool
      .use: session =>
        for
          cmd <- session.prepare(CreateUserCommand)
          id  <- UserId.random[F]
          _   <- cmd.execute(id, email, firstName, lastName, passwordHash)
        yield Right(User(id, email, firstName, lastName))
      .recoverWith:
        case SqlState.UniqueViolation(ex)
            if ex.constraintName.contains("users_email_key") =>
          MonadCancelThrow[F].pure(Left(EmailAlreadyInUse(email)))

  override def find(userId: UserId): F[Option[User]] =
    Scribe[F].trace(s"Fetching user with id: $userId") *>
      sessionPool.use: session =>
        for
          query <- session.prepare(ReadUserQuery)
          user  <- query option userId
        yield user

end SkunkUsersInterpreter

object SkunkUsersInterpreter:

  private val CreateUserCommand
      : Command[(UserId, Email, FirstName, LastName, PasswordHash)] =
    sql"""INSERT INTO users
            VALUES ($userId, $email, $firstName, $lastName, $passwordHash);
            """.command

  private val ReadUserQuery: Query[UserId, User] =
    sql"""SELECT id, email, first_name, last_name
         FROM users
         WHERE id=$userId;"""
      .query(userId *: email *: firstName *: lastName)
      .to[User]

end SkunkUsersInterpreter
