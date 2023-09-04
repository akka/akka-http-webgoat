package akka.http.webgoat

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer

import scala.util.Failure
import scala.util.Success
import scala.util.control.NonFatal

object BootWebGoat extends App {
  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  try {
    val bindingF =
      Http()
        .newServerAt("localhost", 8080)
        .bindFlow(Routes.root)

    bindingF.onComplete {
      case Success(binding) =>
        println(s"Binding successful at ${binding.localAddress}")

      case Failure(e) =>
        println(s"Binding failed with ${e.getMessage}")
        e.printStackTrace()
        system.terminate()
    }
  } catch {
    case NonFatal(e) =>
      e.printStackTrace()
      system.terminate()
  }
}
