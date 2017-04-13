package com.github.naferx.cluster

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster


object SimpleClusterListener {
  def props(): Props = Props(new SimpleClusterListener)
}

final class SimpleClusterListener extends Actor with ActorLogging {
  import akka.cluster.ClusterEvent._

  val cluster: Cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  override def receive: Receive = {
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info(
        "Member is Removed: {} after {}",
        member.address, previousStatus)
    case e: MemberEvent => // ignore
      log.info("MemberEvent detected {}", e)
    case _: Any => log.info("Receiving messages...")
  }

}