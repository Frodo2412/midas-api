import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.*

object Dependencies {

  object CatsMtl {
    private val catsVersion: String = "1.4.0"

    val dep = "org.typelevel" %% "cats-mtl" % catsVersion
  }

  object CatsEffect {
    private val catsEffectVersion = "3.5.4"

    val dep = "org.typelevel" %% "cats-effect" % catsEffectVersion
  }

  object Circe {
    val circeVersion = "0.14.7"

    val core    = "io.circe" %% "circe-core"    % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
    val parser  = "io.circe" %% "circe-parser"  % circeVersion
  }

  object Ciris {
    private val cirisVersion = "3.6.0"

    val dep = "is.cir" %% "ciris" % cirisVersion
  }

  object Flyway {
    val flywayVersion = "10.15.2"

    val core             = "org.flywaydb" % "flyway-core" % flywayVersion
    val databasePostgres =
      "org.flywaydb" % "flyway-database-postgresql" % flywayVersion % Runtime
  }

  object Http4s {
    private val emberVersion = "0.23.27"

    val emberServer = "org.http4s" %% "http4s-ember-server" % emberVersion
  }

  object Iron {
    private val ironVersion = "2.6.0"

    val core  = "io.github.iltotore" %% "iron"       % ironVersion
    val cats  = "io.github.iltotore" %% "iron-cats"  % ironVersion
    val circe = "io.github.iltotore" %% "iron-circe" % ironVersion
    val ciris = "io.github.iltotore" %% "iron-ciris" % ironVersion
    val skunk = "io.github.iltotore" %% "iron-skunk" % ironVersion
  }

  object Monocle {
    private val monocleVersion = "3.2.0"

    val core    = "dev.optics" %% "monocle-core"  % monocleVersion
    val `macro` = "dev.optics" %% "monocle-macro" % monocleVersion
  }

  object Natchez {
    private val natchezVersion = "0.3.5"

    val core   = "org.tpolecat" %% "natchez-core"   % natchezVersion
    val jaeger = "org.tpolecat" %% "natchez-jaeger" % natchezVersion
  }

  object Postgres {
    private val postgresVersion = "42.7.3"

    val dep = "org.postgresql" % "postgresql" % postgresVersion
  }

  object Scribe {
    private val scribeVersion = "3.15.0"

    val core = "com.outr" %% "scribe"       % scribeVersion
    val cats = "com.outr" %% "scribe-cats"  % scribeVersion
    val sl4j = "com.outr" %% "scribe-slf4j" % scribeVersion
  }

  object Skunk {
    private val skunkVersion = "0.6.3"

    val core = "org.tpolecat" %% "skunk-core" % skunkVersion
  }

  object TestContainers {
    private val testContainersVersion = "1.19.8"

    val core =
      "org.testcontainers" % "testcontainers" % testContainersVersion % Test
    val postgres =
      "org.testcontainers" % "postgresql" % testContainersVersion % Test
  }

  object Tsec {
    private val tsecVersion = "0.5.0"

    val common   = "io.github.jmcardon" %% "tsec-common"   % tsecVersion
    val password = "io.github.jmcardon" %% "tsec-password" % tsecVersion
  }

  object Weaver {
    private val weaverVersion = "0.8.4"

    val cats       = "com.disneystreaming" %% "weaver-cats" % weaverVersion % Test
    val scalacheck =
      "com.disneystreaming" %% "weaver-scalacheck" % weaverVersion % Test
  }

  val common: Seq[ModuleID] = Seq(
    CatsEffect.dep,
    CatsMtl.dep,
    Circe.core,
    Circe.generic,
    Circe.parser,
    Ciris.dep,
    Flyway.core,
    Flyway.databasePostgres,
    Http4s.emberServer,
    Iron.core,
    Iron.cats,
    Iron.circe,
    Iron.ciris,
    Iron.skunk,
    Monocle.core,
    Monocle.`macro`,
    Natchez.core,
    Natchez.jaeger,
    Postgres.dep,
    Scribe.core,
    Scribe.cats,
    Scribe.sl4j,
    Skunk.core,
    TestContainers.core,
    TestContainers.postgres,
    Tsec.common,
    Tsec.password,
    Weaver.cats,
    Weaver.scalacheck
  )

}
