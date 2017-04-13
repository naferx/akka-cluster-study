
name := "akka-cluster-study"

version := "1.0"

scalaVersion := "2.11.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster").map( _ % "2.5.0")