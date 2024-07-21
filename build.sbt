Global / excludeLintKeys ++= Set(idePackagePrefix, mainClass)

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root = (project in file("."))
  .settings(
    name             := "midas",
    idePackagePrefix := Some("com.principate.midas"),
    mainClass        := Some("com.principate.midas.Main"),
    libraryDependencies ++= Dependencies.common ++ Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % "1.10.13",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.10.13"
    )
  )
  .dependsOn(lib, security)
  .aggregate(lib, security)

lazy val security = (project in file("domains/security"))
  .settings(
    name             := "security",
    idePackagePrefix := Some("com.principate.midas.security"),
    libraryDependencies ++= Dependencies.common ++ Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core"          % "1.10.14",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % "1.10.14",
      "com.softwaremill.sttp.tapir" %% "tapir-iron"          % "1.10.14",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.13",
      Dependencies.Tsec.common,
      Dependencies.Tsec.password,
      Dependencies.CatsEffect.dep,
      Dependencies.Iron.core,
      Dependencies.Iron.cats,
      Dependencies.Iron.circe,
      Dependencies.Iron.skunk,
      Dependencies.Monocle.core,
      Dependencies.Monocle.`macro`
    )
  )

lazy val lib = (project in file("modules/lib"))
  .settings(
    name             := "lib",
    idePackagePrefix := Some("com.principate.midas.lib"),
    libraryDependencies ++= Seq(
      Dependencies.CatsEffect.dep,
      Dependencies.Iron.core,
      Dependencies.Iron.cats,
      Dependencies.Iron.circe,
      Dependencies.Iron.skunk,
      Dependencies.Monocle.core,
      Dependencies.Monocle.`macro`
    )
  )
