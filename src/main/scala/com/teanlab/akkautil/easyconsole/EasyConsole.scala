package com.teanlab.akkautil.easyconsole

import akka.actor._
import akka.io.{IO, Tcp}
import scala.concurrent.Future
import akka.util.ByteString
import java.net.InetSocketAddress

class EasyConsole(
  val inet: InetSocketAddress,
  val welcomeMessage: String,
  val func: EasyConsole.Func
) extends Actor with ActorLogging {

  import context.system
  import Tcp._
  val name = s"ConsoleNet(${inet.getHostName}:${inet.getPort})"
  val welcome = welcomeMessage + "\n"

  val manager = IO(Tcp)
  manager ! Bind(self, inet, 1)

  val binding:Receive = {
    case Bound(addr) =>
      log.info(s"${name}: Bounded")
      context become (bound,false)
    case CommandFailed(cmd: Bind) =>
      log.error(s"${name}: Binding has failed")
      context stop self
  }

  val bound:Receive = {
    case Connected(remote, local) =>
      val servantName = s"Client(${remote.getHostName}:${remote.getPort})"
      log.info(s"${name}: New Connection from $servantName")
      val servant = context actorOf Servant.props(name, servantName, func)
      sender ! Register(servant)
      sender ! Write(ByteString(welcome))
  }

  def receive = binding

}

object EasyConsole {

  type Func = PartialFunction[List[String],String]

  def props(
    host: String,
    port: Int,
    welcome: String,
    func: Func
  ):Props = props( new InetSocketAddress(host, port), welcome, func)

  def props(inet: InetSocketAddress, welcome:String, func:Func):Props = Props {
    new EasyConsole(inet, welcome, func)
  }
}
