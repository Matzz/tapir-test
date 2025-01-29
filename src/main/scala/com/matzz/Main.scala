package com.matzz

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation._
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.StdIn.readLine

object Main extends App {

  val namePath: EndpointInput.PathCapture[String] = path[String]("name")
  val nameQueryPath: EndpointInput.Query[Option[String]] = query[Option[String]]("sportId")

  val helloServerEndpoint: ServerEndpoint[Any, Future] = endpoint.get
    .in("hello" / "1" / "2" / namePath)
    .name("hello1")
    .in(nameQueryPath)
    .out(stringBody)
    .serverLogicSuccess {
      case (name, nameOpt) => Future.successful(s"Hello ${name} ${nameOpt.getOrElse("")}\n")
    }
  val helloServerEndpoint2: ServerEndpoint[Any, Future] = endpoint.get
    .in("hello" / "2" / "1" / namePath)
    .name("hello2")
    .out(stringBody)
    .serverLogicSuccess(name => Future.successful(s"Hello2 ${name}\n"))



  implicit val system: ActorSystem = ActorSystem.create()

  def route(endpoint: ServerEndpoint[Any, Future]) =
    AkkaHttpServerInterpreter().toRoute(endpoint)

  val program = Http()
    .newServerAt("localhost", 8080)
    .bind(
      route(helloServerEndpoint) ~
        route(helloServerEndpoint2) ~
        AkkaRoutes.raw
    )
    .map { binding =>
      println(
        "HTTP service listening on: " +
          s"http://${binding.localAddress.getHostName}:${binding.localAddress.getPort}/"
      )

      readLine()
      binding.unbind()
    }


  Await.result(program, Duration.Inf)
}