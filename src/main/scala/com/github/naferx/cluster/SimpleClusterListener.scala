package com.github.naferx.cluster

import akka.actor.{Actor, ActorLogging}

final class SimpleClusterListener extends Actor with ActorLogging {

  import akka.cluster.Cluster

  val cluster: Cluster = Cluster(context.system)

  override def receive: Receive = {
    case _: Any => log.info("Receiving messages...")
  }
}