package com.principate.midas

import cats.effect.IO
import cats.effect.Resource
import cats.effect.ResourceApp
import cats.effect.std.Random
import cats.effect.std.SecureRandom
import natchez.EntryPoint
import natchez.Trace
import natchez.Trace.ioTraceForEntryPoint
import natchez.jaeger.Jaeger
import scribe.Level
import scribe.Scribe
import scribe.cats.*

import java.net.URI

import bootstrap.*
import config.AppConfig

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
                               postgres.password.value
                             )
          apiRoutes        = Api[IO](postgres).routes
          _               <- Server.start[IO](apiRoutes)
        yield ()

  private val configs: Resource[IO, AppConfig] = Resource eval AppConfig[IO]

  private val tracer: Resource[IO, EntryPoint[IO]] =
    Jaeger.entryPoint("midas", Some(new URI("http://localhost:8080"))):
      configuration => IO(configuration.getTracer)

  private val secureRandom: Resource[IO, Random[IO]] =
    Resource eval SecureRandom.javaSecuritySecureRandom[IO]

end Main
