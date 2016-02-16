
import org.scalatest._
import akka.testkit._
import akka.actor._
import akka.io.Tcp
import akka.util.ByteString
import com.teanlab.akkautil.consolenet._
import scala.concurrent.duration._

class MySpec(
  _system: ActorSystem
) extends TestKit(_system) 
    with ImplicitSender
    with WordSpecLike
    with Matchers 
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val helloResponse = "Hello, Nice to meet you :)"

  val func:ConsoleNet.Func = {
    case "hello" => helloResponse
    case "help" => "Ops!"
  }

  "First" must {
    "success" in {
      
      val servant = _system.actorOf(Servant.props("A","B", func))

      servant ! Tcp.Received(ByteString("help\nhe"))
      receiveN(1)

      servant ! Tcp.Received(ByteString("llo\nOPS"))
      expectMsgPF(1 seconds){
        case Tcp.Write(output: ByteString, _) =>
          val its = new String(output.toArray)
          info(its)
          assert( its == helloResponse)
        case unexp => fail(s"Unexpected output with type: ${unexp.getClass}")
      }
    }
  }

}
