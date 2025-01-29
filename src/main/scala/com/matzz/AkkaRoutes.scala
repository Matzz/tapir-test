package com.matzz

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{Segment, complete, get, path}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatcher._

import scala.concurrent.Future


object AkkaRoutes {
  val raw: Route = {
    get {
      path("hello" / "3" / Segment / "end") { name =>
        println(s"Hello ${name}\n")
        complete(
          StatusCodes.OK,
          HttpEntity(
            `application/json`,
            "{}",
          ),
        )
      }
    }
  }
}
