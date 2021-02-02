ThisBuild / name := "functional-gui"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.4"

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.2" % "test",
    "io.monix" %% "monix" % "3.3.0"
  )
)
lazy val core = (project in file("core"))
  .settings(commonSettings)
lazy val swing = (project in file("swing"))
  .settings(commonSettings)
  .dependsOn(core)

lazy val root = (project in file("."))
  .aggregate(swing, core)