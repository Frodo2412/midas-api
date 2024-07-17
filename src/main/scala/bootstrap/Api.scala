package com.principate.midas
package bootstrap

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Random
import org.http4s.HttpRoutes
import scribe.Scribe
import skunk.Session
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import security.SecurityRoutes

class Api[F[_]: Async: Random: Scribe](sessionPool: Resource[F, Session[F]]):

  private val healthEndpoint: ServerEndpoint[Any, F] =
    endpoint.get
      .in("health")
      .out(stringJsonBody)
      .serverLogicSuccessPure[F](_ => "Still alive...")

  private val security: List[ServerEndpoint[Fs2Streams[F], F]] =
    SecurityRoutes[F](sessionPool).routes

  private val endpoints: List[ServerEndpoint[Fs2Streams[F], F]] =
    healthEndpoint :: security

  private val docs: List[ServerEndpoint[Fs2Streams[F], F]] =
    SwaggerInterpreter()
      .fromServerEndpoints[F](
        endpoints,
        "Midas API",
        "0.1.1"
      )

  private val serverOptions = Http4sServerOptions.default

  def routes: HttpRoutes[F] =
    Http4sServerInterpreter[F](serverOptions).toRoutes(
      endpoints ++ docs
    )

end Api
