val CatsEffectVersion = "3.4.9"
val CirceVersion = "0.14.3"
val DoobieVersion = "1.0.0-RC1"
val Http4sVersion = "0.23.18"
val LogbackVersion = "1.2.11"
val MunitCatsEffectVersion = "1.0.7"
val MunitVersion = "0.7.29"
val NewtypeVersion = "0.4.4"
val OtjPgEmbeddedVersion = "1.0.1"
val PureConfigVersion = "0.17.4"
val ScalaMockVersion = "5.1.0"
val ScalaTestVersion = "3.2.15"
val SvmSubsVersion = "20.2.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "shoppingcart",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.10",

    libraryDependencies ++= Seq(
      "org.http4s"                %% "http4s-ember-server"    % Http4sVersion,
      "org.http4s"                %% "http4s-ember-client"    % Http4sVersion,
      "org.http4s"                %% "http4s-circe"           % Http4sVersion,
      "org.http4s"                %% "http4s-dsl"             % Http4sVersion,
      "org.typelevel"             %% "cats-effect"            % CatsEffectVersion,
      "io.circe"                  %% "circe-generic"          % CirceVersion,
      "org.tpolecat"              %% "doobie-core"            % DoobieVersion,
      "org.tpolecat"              %% "doobie-postgres"        % DoobieVersion,
      "org.tpolecat"              %% "doobie-postgres-circe"  % DoobieVersion,
      "org.tpolecat"              %% "doobie-hikari"          % DoobieVersion,
      "com.github.pureconfig"     %% "pureconfig"             % PureConfigVersion,
      "com.github.pureconfig"     %% "pureconfig-cats-effect" % PureConfigVersion,
      "ch.qos.logback"            % "logback-classic"         % LogbackVersion          % Runtime,
      "org.scalameta"             %% "svm-subs"               % SvmSubsVersion,
      "io.estatico"               %% "newtype"                % NewtypeVersion,
      "org.scalameta"             %% "munit"                  % MunitVersion            % Test,
      "org.typelevel"             %% "munit-cats-effect-3"    % MunitCatsEffectVersion  % Test,
      "org.scalatest"             %% "scalatest"              % ScalaTestVersion        % Test,
      "org.scalamock"             %% "scalamock"              % ScalaMockVersion        % Test,
      "org.tpolecat"              %% "doobie-munit"           % DoobieVersion           % Test,
      "com.opentable.components"  % "otj-pg-embedded"         % OtjPgEmbeddedVersion    % Test
    ),
    scalacOptions ++= Seq( // Only add those not already in io.github.davidgregory084" % "sbt-tpolecat
      "-Ymacro-annotations"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
