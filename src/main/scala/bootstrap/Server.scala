package com.principate.midas
package bootstrap

import cats.effect.{Async, Resource}
import com.comcast.ip4s.{host, port}
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import scribe.Scribe

object Server:

  private val host = host"0.0.0.0"
  private val port = port"8080"

  def start[F[_]: Async: Scribe](routes: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(host)
      .withPort(port)
      .withHttpApp(routes.orNotFound)
      .build
      .evalTap(_ => Scribe[F].info(s"Its alive at $host:$port"))
    
end Server
