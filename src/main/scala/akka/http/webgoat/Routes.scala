package akka.http.webgoat

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import scala.concurrent.duration._

object Routes extends Directives {
  lazy val root: Route = concat(
    pathPrefix("commandInjectionSimple")(commandInjectionSimple),
    pathPrefix("commandInjectionCallMethod")(commandInjectionCallMethod),
    pathPrefix("commandInjectionNestedParameterDirectives")(commandInjectionNestedParameterDirectives),
    pathPrefix("commandInjectionMoreParameters")(commandInjectionMoreParameters),
    pathPrefix("commandInjectionMultipleParametersByConjunction")(commandInjectionMultipleParametersByConjunction),
    pathPrefix("commandInjectionMultipleParametersByAlternative")(commandInjectionMultipleParametersByAlternative),
    pathPrefix("commandInjectionParameterInRouteAlternative")(commandInjectionParameterInRouteAlternative),
    pathPrefix("commandInjectionFromPathSegment")(commandInjectionFromPathSegment),
    pathPrefix("commandInjectionDirectiveValue")(commandInjectionDirectiveValue),
    pathPrefix("commandInjectionParameterAbstract")(commandInjectionParameterAbstract),
    pathPrefix("commandInjectiondAsync")(commandInjectiondAsync),
    pathPrefix("commandInjectionFromPathSegment")(commandInjectionFromPathSegment),
    pathPrefix("commandInjectionFromFormField")(commandInjectionFromFormField),
    pathPrefix("commandInjectionFromCookie")(commandInjectionFromCookie),
    pathPrefix("getFileFromParameter")(getFileFromParameter),
    pathPrefix("getFileFromFormField")(getFileFromFormField),
    pathPrefix("getFileFromPathSegment")(getFileFromPathSegment),
    pathPrefix("getFromDirectoryFromParameter")(getFromDirectoryFromParameter),
    pathPrefix("getFromBrowseableDirectoryFromParameter")(getFromBrowseableDirectoryFromParameter),
    pathPrefix("runClientRequestFromParameter")(runClientRequestFromParameter),
    pathPrefix("runClientRequestFromParameter")(runClientRequestFromParameter),
    pathPrefix("runClientRequestWithUriPartFromParameter")(runClientRequestWithUriPartFromParameter),
    pathPrefix("unrestrictedAccessControlAllowOrigin")(unrestrictedAccessControlAllowOrigin)
  )

  // A command injection example from a path parameter with different styles of routes

  lazy val commandInjectionSimple =
    parameter("cmd") { cmd =>
      import sys.process._
      val result = s"/bin/bash $cmd".!!
      complete(result)
    }

  def execute(cmd: String): String = {
    import sys.process._
    s"/bin/bash $cmd".!!
  }

  // process call behind method call
  lazy val commandInjectionCallMethod =
    parameter("cmd") { cmd =>
      complete(execute(cmd))
    }

  // process call nested in directives
  lazy val commandInjectionNestedParameterDirectives =
    parameter("cmd") { cmd =>
      parameter("other") { _ =>
        complete(execute(cmd))
      }
    }

  // command injection when vulnerable parameter is not the first one
  lazy val commandInjectionMoreParameters =
    parameter("firstParam".as[Int], "cmd") { (i, cmd) =>
      complete(execute(cmd))
    }

  // command injection when vulnerable parameter is extracted via directive conjunction
  lazy val commandInjectionMultipleParametersByConjunction =
    (get & parameter("firstParam".as[Int]) & parameter("cmd")) { (i, cmd) =>
      complete(execute(cmd))
    }

  // command injection when vulnerable parameter is extracted via directive alternative
  lazy val commandInjectionMultipleParametersByAlternative =
    (parameter("command") | parameter("cmd")) { cmd =>
      complete(execute(cmd))
    }

  // process call when parameter is only used in an alternative route
  lazy val commandInjectionParameterInRouteAlternative =
    parameter("cmd") { cmd =>
      concat(
        path("xyz")(reject),
        complete(execute(cmd))
      )
    }

  private val getCommandParameter: Directive1[String] =
    parameter("cmd")

  // command injection when directive is abstracted
  lazy val commandInjectionDirectiveValue =
    getCommandParameter { cmd =>
      complete(execute(cmd))
    }

  def executeAndComplete(cmd: String): Route = complete(execute(cmd))
  // command injection when directive, call, and completion is abstract
  // (not common in this form but not impossible in more contrived ones)
  lazy val commandInjectionParameterAbstract =
    getCommandParameter(executeAndComplete)

  // process call nested in async processing
  lazy val commandInjectiondAsync =
    parameter("cmd") { cmd =>
      // future and value is ignored but the inner blocks will be executed asynchronously after a while
      // to break the linear call stack
      provideFutureCompletedInAWhile { fut =>
        onComplete(fut) { v =>
          complete(execute(cmd))
        }
      }
    }

  // other command injection samples from other sources of user input (route style matrix was not expanded for those)

  // process call from path segment
  lazy val commandInjectionFromPathSegment =
    path("execute" / Segment) { cmd =>
      complete(execute(cmd))
    }

  // process call from form field
  lazy val commandInjectionFromFormField =
    formField("cmd") { cmd =>
      complete(execute(cmd))
    }

  // process call from cookie
  lazy val commandInjectionFromCookie =
    cookie("command") { cookiePair =>
      complete(execute(cookiePair.value))
    }

  // get files from various user input (route style matrix was not expanded for those)

  lazy val getFileFromParameter =
    parameter("fileName") { fileName =>
      getFromFile(fileName)
    }
  lazy val getFileFromFormField =
    formField("fileName") { fileName =>
      getFromFile(fileName)
    }
  lazy val getFileFromPathSegment =
    path("get" / Segment) { fileName =>
      getFromFile(fileName)
    }

  // other target directives (route style matrix was not expanded for those)

  lazy val getFromDirectoryFromParameter =
    parameter("dirName") { fileName =>
      getFromDirectory(fileName)
    }

  lazy val getFromBrowseableDirectoryFromParameter =
    parameter("dirName") { fileName =>
      getFromBrowseableDirectory(fileName)
    }

  // user supplied client request
  lazy val runClientRequestFromParameter =
    extractActorSystem { implicit system =>
      parameter("uri") { uri =>
        val request = HttpRequest(uri = uri)
        val responseF = Http().singleRequest(request)
        complete(responseF)
      }
    }

  // user supplied client request
  lazy val runClientRequestWithUriPartFromParameter =
    extractActorSystem { implicit system =>
      parameter("uri") { uri =>
        val request = HttpRequest(uri = s"https://localhost:12345/$uri")
        val responseF = Http().singleRequest(request)
        complete(responseF)
      }
    }

  // XSS: disabling CORS is a security risk
  lazy val unrestrictedAccessControlAllowOrigin =
    respondWithHeader(headers.`Access-Control-Allow-Origin`.*) {
      complete("Hello, I'm unsafe")
    }

  // some arbitrary directive that returns a future that will only be completed after a while
  private def provideFutureCompletedInAWhile: Directive1[Future[Int]] =
    extractActorSystem.flatMap { system =>
      extractExecutionContext.flatMap { implicit ec =>
        import akka.pattern.after
        provide(after(1.second, system.scheduler)(Future(1)))
      }
    }

  /*
  Things to test:

  There are several dimension making up a matrix of potential things to catch:
    * user input extraction
    * vulnerable call
    * code patterns i.e. complicating circumstances

  Vulnerabilities:

    SSRF:

    * create process
    * get file
    * get resource
    * client request

    XSS:



    CSRF:
    * disabling CORS by adding a Access-Control-Allow-Origin: * header

  Complicating (but common) code structures:
    * asynchronous request completion (using onSuccess or complete(Future))
    * different ways of combining routes
      * simple nesting
      * directive conjunction and alternatives
      * route alternatives
      * Mixing in routes
    * different ways of extracting information from requests
      * parameter / formField for one parameter
      * for multiple parameters
      * nested directives
      * extract from request object (uncommon)
    * Custom directives

  */
}
