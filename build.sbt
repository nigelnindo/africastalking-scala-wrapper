name := "AT-Scala"

version := "1.0"

val commonSettings = Seq(
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")
)

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

lazy val root = (project in file(".")).
  settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9",
      "com.typesafe.akka" %% "akka-http" % "10.0.9",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.0.9" % Test

    )
  )

