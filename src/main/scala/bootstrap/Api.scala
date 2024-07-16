package com.principate.midas
package bootstrap

import cats.effect.Async
import org.http4s.HttpRoutes
import scribe.Scribe
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class Api[F[_]: Async: Scribe]:

  private val healthEndpoint: ServerEndpoint[Any, F] =
    endpoint.get
      .in("health")
      .out(stringJsonBody)
      .serverLogicSuccessPure[F](_ => "Still alive...")

  private val docs = SwaggerInterpreter()
    .fromServerEndpoints[F](
      List(healthEndpoint),
      "Midas API",
      "0.1.1"
    )

  def routes: HttpRoutes[F] =
    Http4sServerInterpreter[F]()
      .toRoutes(healthEndpoint :: docs)

end Api
