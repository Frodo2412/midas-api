package com.principate.midas.security
package interpreters

import algebras.Users
import models.User

import cats.effect.{MonadCancelThrow, Resource}
import cats.syntax.all.*
import com.principate.midas.lib.newtypes.GenUUID
import com.principate.midas.security.algebras.Users.CreateUserError.EmailAlreadyInUse
import org.postgresql.util.PSQLException
import scribe.Scribe
import skunk.codec.all.uuid
import skunk.codec.text.*
import skunk.implicits.sql
import skunk.{Command, Query, Session, SqlState}

import java.util.UUID

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
      s"Creating user: { email: $email, firstName: $firstName, lastName: $lastName"
    ) *>
      sessionPool
        .use: session =>
          for
            cmd <- session.prepare(CreateUserCommand)
            _   <- Scribe[F].trace(cmd.toString)
            id  <- GenUUID[F].make[UUID]
            _   <- cmd.execute(id, email, firstName, lastName, passwordHash)
          yield Right(User(id, email, firstName, lastName))
        .recoverWith:
          case SqlState.UniqueViolation(ex)
              if ex.columnName.contains("email") =>
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
    sql"""SELECT id, email, first_name, last_name FROM users WHERE id=$uuid;"""
      .query(uuid *: text *: text *: text)
      .to[User]

end SkunkUsersInterpreter
