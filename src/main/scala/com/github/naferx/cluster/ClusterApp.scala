package com.github.naferx.cluster

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object ClusterApp extends App {

  println("Starting clustered app...")

  val config = ConfigFactory.load("seed")

  val system = ActorSystem("ClusterApp", config)

  val clusterListener = system.actorOf(SimpleClusterListener.props())

 }
