package com.principate.midas

import bootstrap.*
import config.AppConfig

import cats.effect.{IO, Resource, ResourceApp}
import natchez.Trace.ioTraceForEntryPoint
import natchez.jaeger.Jaeger
import natchez.{EntryPoint, Trace}
import scribe.cats.*
import scribe.{Level, Scribe}

import java.net.URI

object Main extends ResourceApp.Forever:

  given Scribe[IO] = scribe
    .Logger("midas")
    .withMinimumLevel(Level.Trace)
    .f[IO]

  override def run(args: List[String]): Resource[IO, Unit] =
    configs.flatMap:
      case AppConfig(postgres) =>
        for
          entryPoint      <- tracer
          given Trace[IO] <- Resource eval ioTraceForEntryPoint(entryPoint)
          postgres        <- Database.connect[IO](
                               postgres.host,
                               postgres.port,
                               postgres.name,
                               postgres.user,
                               Some(postgres.password)
                             )
          apiRoutes        = Api[IO].routes
          _               <- Server.start[IO](apiRoutes)
        yield ()

  private val configs: Resource[IO, AppConfig] = Resource eval AppConfig[IO]

  private val tracer: Resource[IO, EntryPoint[IO]] =
    Jaeger.entryPoint("midas", Some(new URI("http://localhost:8080"))):
      configuration => IO(configuration.getTracer)

end Main
