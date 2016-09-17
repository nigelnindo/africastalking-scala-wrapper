name := "AT-Scala"

version := "1.0"

val commonSettings = Seq(
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*)

lazy val api = (project in file("api")).
  settings(commonSettings: _*)
  .settings(
    libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"
  )