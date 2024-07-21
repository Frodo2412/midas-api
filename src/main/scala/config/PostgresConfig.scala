package com.principate.midas
package config

import cats.effect.Async
import cats.syntax.all.*
import ciris.*
import io.github.iltotore.iron.Not
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.ciris.given
import io.github.iltotore.iron.constraint.all.*

import config.PostgresConfig.*

case class PostgresConfig(
    host: DatabaseHost,
    port: DatabasePort,
    name: DatabaseName,
    user: DatabaseUser,
    password: Secret[DatabasePassword]
)

object PostgresConfig:

  def apply[F[_]: Async]: F[PostgresConfig] =
    (
      host,
      port,
      name,
      user,
      password
    ).mapN(PostgresConfig.apply).load[F]

  private def host: ConfigValue[Effect, DatabaseHost] =
    env("DATABASE_HOST").as[DatabaseHost].default("localhost")

  private def port: ConfigValue[Effect, DatabasePort] =
    env("DATABASE_PORT").as[DatabasePort].default(5432)

  private def name: ConfigValue[Effect, DatabaseName] =
    env("DATABASE_NAME").as[DatabaseName].default("midas")

  private def user: ConfigValue[Effect, DatabaseUser] =
    env("DATABASE_USER").as[DatabaseUser].default("user")

  private def password: ConfigValue[Effect, Secret[DatabasePassword]] =
    env("DATABASE_PASSWORD").as[DatabasePassword].default("password").secret

  type DatabaseHost = String :| Not[Blank]
  object DatabaseHost extends RefinedTypeOps.Transparent[DatabaseHost]

  type DatabasePort = Int :| Positive
  object DatabasePort extends RefinedTypeOps.Transparent[DatabasePort]

  type DatabaseName = String :| Not[Blank]
  object DatabaseName extends RefinedTypeOps.Transparent[DatabaseName]

  type DatabaseUser = String :| Not[Blank]
  object DatabaseUser extends RefinedTypeOps.Transparent[DatabaseUser]

  type DatabasePassword = String :| Not[Blank]
  object DatabasePassword extends RefinedTypeOps.Transparent[DatabasePassword]

end PostgresConfig
