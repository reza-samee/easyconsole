// in the name of ALLAH

import akka.actor._
import com.teanlab.akkautil.easyconsole._
import com.teanlab.cli._
import scala.concurrent.Future
import akka.util.ByteString

object Main extends SimpleApp {

  type Params = (String, Int)
  
  override val usage = 
    """Usage: <options>
    | Options:
    |   -host $string
    |   -port $int
    |""".stripMargin

  override val params = for {
    host <- required[String]("host")
    port <- required[Int]("port")
  } yield (host, port)

  override def func = {
    case (host, port) =>
      val system = ActorSystem()
      val prop = EasyConsole.props(host, port, "Welcome; try 'help'", netfunc(system))
      val cnet = system actorOf (prop,"consolenet")
      system.whenTerminated.map(_ => code.Success)
    case _ =>
      println(usage)
      Future successful code.UsageError
  }

  def netfunc(system: ActorSystem): EasyConsole.Func = {
    case "help" :: Nil => """ Usage:
    |   help
    |   sayhello
    |   halt
    |""".stripMargin
    case "sayhello" :: Nil => "Hello :)"
    case "halt" :: Nil => 
      system.terminate
      "Going to shutdown system"
  }

   

}
