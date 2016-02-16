// in the name of ALLAH

def mkCommonSettings(prjName: String, dirPath: String) = Seq(
  organization := "com.teanlab",
  name := prjName,
  version := "0.1.0",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps"
  )
)

lazy val root = (project in file(".")).
  settings(mkCommonSettings("akka-easyconsole","."):_*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.1",
      "com.typesafe.akka" %% "akka-testkit" % "2.4.1" % Test,
      "org.scalatest" %% "scalatest" % "2.2.6" % Test,
      "org.scalaz" %% "scalaz-core" % "7.1.1"
      // "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
    )
  )


lazy val example = (project in file("example")).
  settings(mkCommonSettings("akka-easyconsole-example","example"):_*).
  settings(
    libraryDependencies ++= Seq(
      "com.teanlab" %% "cli" % "0.1.0"
    )
  ).
  dependsOn(root)
    
