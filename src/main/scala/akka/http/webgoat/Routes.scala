package akka.http.webgoat

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route

object Routes extends Directives {
  lazy val root: Route = complete("Hello World")
}
