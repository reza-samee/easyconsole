package com.teanlab.akkautil.easyconsole

import akka.actor._
import akka.io.Tcp
import akka.util.ByteString
import scalaz._, Scalaz._
import scala.concurrent.Future
import scala.util.{Try, Success => StdSuccess, Failure => StdFailure}

class Servant(
  consoleName: String,
  name: String,
  func: EasyConsole.Func
) extends Actor with ActorLogging {

  import Tcp._

  var commands: List[String] = nil
  var buffer: ByteString = ByteString.empty

  def receive: Receive = {
    case Received(input) => 
      val (b,lines) = Servant.getLines(buffer ++ input)
      buffer = b
      lines map(_.split(" ").toList)foreach {
        case line if func isDefinedAt line => 
          sender ! Write(ByteString(func(line)+"\n"))
        case unsupported =>
          sender ! Write(ByteString(s"Unsupported: ${unsupported}\n"))
      }
    case CommandFailed(cmd) =>
      log.error(s"$consoleName/$name: Failed Command (I/O)")
      context stop self
    case sig:ConnectionClosed =>
      log.warning(s"$consoleName/$name: Connection has closed")
      context stop self
  }
}

object Servant {

  def props(
    consoleName:String,
    name: String,
    func: EasyConsole.Func
  ) = Props { new Servant(consoleName, name, func) }

  val newline = "\n".getBytes apply 0
  val resetline = "\r".getBytes apply 0

  val tokenizer = State[ByteString,Option[ByteString]] { input =>
    input indexOf newline match {
      case pos if pos >= 0 => 
        val (end:Int,next:Int) = 
          if (input(pos-1) == resetline) (pos-1, pos+1)
          else if( input(pos+1) == resetline) (pos, pos+2)
          else (pos,pos+1)
        (
          input.slice(next, input.length),
          Option(input.slice(0, end))
        )
      case error => (input,none[ByteString])
    }
  }

  @scala.annotation.tailrec
  final def parse(
    input: ByteString, 
    stack: List[ByteString] = Nil
  ):(ByteString,List[ByteString]) =
    tokenizer run input match {
      case (remained, None) => (remained, stack)
      case (remained, Some(line)) => parse(remained, line :: stack)
    }

  def getLines(input:ByteString):(ByteString,List[String]) = {
    val (remained, list) = parse(input, nil)
    (remained, list.map(i =>new String(i.toArray)))
  }

}
