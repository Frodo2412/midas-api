package com.principate.midas
package bootstrap

import cats.effect.Async
import cats.effect.MonadCancelThrow
import cats.effect.Resource
import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Network
import natchez.Trace
import org.flywaydb.core.Flyway
import scribe.Scribe
import skunk.Session
import skunk.codec.all.text
import skunk.implicits.sql

import config.PostgresConfig.*

object Database:

  def connect[F[_]: Async: Trace: Network: Console: Scribe](
      host: DatabaseHost,
      port: DatabasePort,
      name: DatabaseName,
      user: DatabaseUser,
      password: DatabasePassword
  ): Resource[F, Resource[F, Session[F]]] =
    Resource.eval(
      migrate(
        host,
        port,
        name,
        user,
        password
      )
    ) *> (Session.pooled[F](
      host,
      port,
      user,
      name,
      Some(password),
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
              dbUser,
              dbPassword
            )
          )
          .map(_.load())
      result <- Sync[F] blocking flyway.migrate()
      _      <- Sync[F].raiseWhen(!result.success)(
                  new Exception(s"Failed to run migrations: ${result.warnings}")
                )
    yield ()

end Database
