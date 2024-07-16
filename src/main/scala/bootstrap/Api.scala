package com.principate.midas
package bootstrap

import cats.effect.Async
import org.http4s.HttpRoutes
import scribe.Scribe
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

class Api[F[_]: Async: Scribe]:

  private val healthEndpoint =
    endpoint.get
      .in("health")
      .out(stringJsonBody)
      .serverLogicSuccessPure[F](_ => "Still alive...")

  def routes: HttpRoutes[F] =
    Http4sServerInterpreter[F]()
      .toRoutes(healthEndpoint)

end Api
