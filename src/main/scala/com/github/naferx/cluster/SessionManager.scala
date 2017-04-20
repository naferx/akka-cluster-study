package com.github.naferx.cluster

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ddata.{DistributedData, LWWMap, LWWMapKey}


object SessionManager {
  import akka.cluster.ddata.Replicator._
  import scala.concurrent.duration._

  case object GetToken
  case object Expired

  final case class SetToken(token: String)

  final case class Session(token: String, id: Int)

  def props(userId: String): Props = Props(new SessionManager(userId))

  private val timeout = 3.seconds
  private val readMajority = ReadMajority(timeout)
  private val writeMajority = WriteMajority(timeout)

}


final class SessionManager(userId: String) extends Actor {

  import SessionManager._
  import akka.cluster.ddata.Replicator._

  val replicator: ActorRef = DistributedData(context.system).replicator
  implicit val cluster: Cluster = Cluster(context.system)

  val DataKey = LWWMapKey[String, Session]("user-" + userId)


  override def receive: PartialFunction[Any, Unit] = receiveGetCart
    //.orElse[Any, Unit](receiveAddItem)
   // .orElse[Any, Unit](receiveRemoveItem)
    //.orElse[Any, Unit](receiveOther)

  //#get-cart
  def receiveGetCart: Receive = {
    case GetToken =>
      replicator ! Get(DataKey, readMajority, Some(sender()))

    case g @ GetSuccess(DataKey, Some(replyTo: ActorRef)) =>
      val data = g.get(DataKey)
     // val cart = Session(data.entries.values.toSet)
    //  replyTo ! cart

    case NotFound(DataKey, Some(replyTo: ActorRef)) =>
      replyTo ! Expired

    case GetFailure(DataKey, Some(replyTo: ActorRef)) =>
      // ReadMajority failure, try again with local read
      replicator ! Get(DataKey, ReadLocal, Some(replyTo))
  }
  //#get-cart
/*
  //#add-item
  def receiveAddItem: Receive = {
    case cmd @ SetToken(item) =>
      val update = Update(DataKey, LWWMap.empty[String, Session], writeMajority, Some(cmd)) {
        cart => updateCart(cart, item)
      }
      replicator ! update
  }
  //#add-item

  def updateCart(data: LWWMap[String, Session], item: Session): LWWMap[Session] =
    data.get(item.token) match {
      case Some(Session(_, _, existingQuantity)) =>
        data + (item.productId -> item.copy(quantity = existingQuantity + item.quantity))
      case None => data + (item.token -> item)
    }

  //#remove-item
  def receiveRemoveItem: Receive = {
    case cmd @ RemoveItem(productId) =>
      // Try to fetch latest from a majority of nodes first, since ORMap
      // remove must have seen the item to be able to remove it.
      replicator ! Get(DataKey, readMajority, Some(cmd))

    case GetSuccess(DataKey, Some(RemoveItem(productId))) =>
      replicator ! Update(DataKey, LWWMap(), writeMajority, None) {
        _ - productId
      }

    case GetFailure(DataKey, Some(RemoveItem(productId))) =>
      // ReadMajority failed, fall back to best effort local value
      replicator ! Update(DataKey, LWWMap(), writeMajority, None) {
        _ - productId
      }

    case NotFound(DataKey, Some(RemoveItem(productId))) =>
    // nothing to remove
  }
  //#remove-item

  def receiveOther: Receive = {
    case _: UpdateSuccess[_] | _: UpdateTimeout[_] =>
    // UpdateTimeout, will eventually be replicated
    case e: UpdateFailure[_]                       => throw new IllegalStateException("Unexpected failure: " + e)
  }
*/
}