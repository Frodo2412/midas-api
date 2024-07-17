package com.principate.midas
package config

import cats.effect.Async
import cats.syntax.all.*
import ciris.ConfigValue
import ciris.Effect

import config.PostgresConfig.*
import lib.newtypes.NewType

case class PostgresConfig(
    host: DatabaseHost,
    port: DatabasePort,
    name: DatabaseName,
    user: DatabaseUser,
    password: DatabasePassword
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
    ciris.env("DATABASE_HOST").default("localhost").map(DatabaseHost.apply)

  private def port: ConfigValue[Effect, DatabasePort] =
    ciris
      .env("DATABASE_PORT")
      .map(_.toInt)
      .default(5432)
      .map(DatabasePort.apply)

  private def name: ConfigValue[Effect, DatabaseName] =
    ciris
      .env("DATABASE_NAME")
      .default("midas")
      .map(DatabaseName.apply)

  private def user: ConfigValue[Effect, DatabaseUser] =
    ciris
      .env("DATABASE_USER")
      .default("user")
      .map(DatabaseUser.apply)

  private def password: ConfigValue[Effect, DatabasePassword] =
    ciris
      .env("DATABASE_PASSWORD")
      .default("password")
      .secret
      .map(_.value)
      .map(DatabasePassword.apply)

  type DatabaseHost = DatabaseHost.T
  object DatabaseHost extends NewType[String]

  type DatabasePort = DatabasePort.T
  object DatabasePort extends NewType[Int]

  type DatabaseName = DatabaseName.T
  object DatabaseName extends NewType[String]

  type DatabaseUser = DatabaseUser.T
  object DatabaseUser extends NewType[String]

  type DatabasePassword = DatabasePassword.T
  object DatabasePassword extends NewType[String]

end PostgresConfig
