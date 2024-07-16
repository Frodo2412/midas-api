package com.principate.midas
package bootstrap

import config.PostgresConfig.*

import cats.effect.{Async, MonadCancelThrow, Resource, Sync}
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import natchez.Trace
import org.flywaydb.core.Flyway
import scribe.Scribe
import skunk.Session
import skunk.codec.all.text
import skunk.implicits.sql

object Database:

  def connect[F[_]: Async: Trace: Network: Console: Scribe](
      host: DatabaseHost,
      port: DatabasePort,
      name: DatabaseName,
      user: DatabaseUser,
      password: Option[DatabasePassword]
  ): Resource[F, Resource[F, Session[F]]] =
    Resource.eval(
      migrate(
        host,
        port,
        name,
        user,
        password.getOrElse(DatabasePassword(""))
      )
    ) *> (Session.pooled[F](
      host.value,
      port.value,
      user.value,
      name.value,
      password.map(_.value),
      5
    ) evalTap checkConnection)

  private def checkConnection[F[_]: MonadCancelThrow: Scribe](
      postgres: Resource[F, Session[F]]
  ): F[Unit] =
    postgres.use:
      _ unique sql"select version();".query(text) flatMap (v =>
        Scribe[F].info(s"Connected to Postgres $v")
      )

  private def url(
      dbHost: DatabaseHost,
      dbPort: DatabasePort,
      dbName: DatabaseName
  ): String = s"jdbc:postgresql://$dbHost:$dbPort/$dbName"

  private def migrate[F[_]: Sync: Scribe](
      dbHost: DatabaseHost,
      dbPort: DatabasePort,
      dbName: DatabaseName,
      dbUser: DatabaseUser,
      dbPassword: DatabasePassword
  ): F[Unit] =
    for
      flyway <-
        Sync[F]
          .delay(Flyway.configure())
          .map(_.locations("filesystem:./migrations"))
          .map(
            _.dataSource(
              url(dbHost, dbPort, dbName),
              dbUser.value,
              dbPassword.value
            )
          )
          .map(_.load())
      result <- Sync[F] blocking flyway.migrate()
      _      <- if result.success then Scribe[F] info result.toString
                else
                  Sync[F] raiseError new Exception(
                    s"Failed to run migrations: ${result.warnings}"
                  )
    yield ()

end Database
