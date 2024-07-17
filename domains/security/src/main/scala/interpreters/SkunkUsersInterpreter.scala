package com.principate.midas.security
package interpreters

import com.principate.midas.lib.newtypes.GenUUID

import cats.effect.MonadCancelThrow
import cats.effect.Resource
import cats.syntax.all.*
import scribe.Scribe
import skunk.Command
import skunk.Query
import skunk.Session
import skunk.SqlState
import skunk.codec.all.uuid
import skunk.codec.text.*
import skunk.implicits.sql

import java.util.UUID

import algebras.Users
import algebras.Users.CreateUserError.EmailAlreadyInUse
import models.User

final class SkunkUsersInterpreter[F[_]: MonadCancelThrow: GenUUID: Scribe](
    sessionPool: Resource[F, Session[F]]
) extends Users[F]:
  import SkunkUsersInterpreter.*

  override def create(
      email: String,
      firstName: String,
      lastName: String,
      passwordHash: String
  ): F[Either[Users.CreateUserError, User]] =
    Scribe[F].trace(
      s"Creating user: { email: $email, firstName: $firstName, lastName: $lastName }"
    ) *> sessionPool
      .use: session =>
        for
          cmd <- session.prepare(CreateUserCommand)
          id  <- GenUUID[F].make[UUID]
          _   <- cmd.execute(id, email, firstName, lastName, passwordHash)
        yield Right(User(id, email, firstName, lastName))
      .recoverWith:
        case SqlState.UniqueViolation(ex)
            if ex.constraintName.contains("users_email_key") =>
          MonadCancelThrow[F].pure(Left(EmailAlreadyInUse(email)))

  override def find(userId: UUID): F[Option[User]] =
    Scribe[F].trace(s"Fetching user with id: $userId") *>
      sessionPool.use: session =>
        for
          query <- session.prepare(ReadUserQuery)
          user  <- query option userId
        yield user

end SkunkUsersInterpreter

object SkunkUsersInterpreter:

  private val CreateUserCommand
      : Command[(UUID, String, String, String, String)] =
    sql"""INSERT INTO users
            VALUES ($uuid, $text, $text, $text, $text);
            """.command

  private val ReadUserQuery: Query[UUID, User] =
    sql"""SELECT id, email, first_name, last_name
         FROM users
         WHERE id=$uuid;"""
      .query(uuid *: text *: text *: text)
      .to[User]

end SkunkUsersInterpreter
