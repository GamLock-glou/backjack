package com.theshow.core

import cats.effect.std.Queue
import cats.effect.{Async, ExitCode, IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server._
import com.theshow.core.http.routes.EventRoutes
import org.http4s.Method
import org.http4s.server.middleware.{CORS, ErrorHandling}
import cats.implicits._

object Main extends IOApp {
  private val eventRouters = EventRoutes.apply();
  private val httpApp = ErrorHandling {
    Seq(
      eventRouters.signInRoutes
    ).reduce(_ <+> _)
  }.orNotFound
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- Async[IO].delay(println("Starting the server"))
      corsMethodSvc = CORS.policy
        .withAllowOriginAll
        .withAllowMethodsIn(Set(Method.GET, Method.POST))
        .withAllowCredentials(false)
        .apply(httpApp)
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"127.0.0.1")
        .withPort(port"4000")
        .withHttpApp(corsMethodSvc)
        .build
        .useForever
    } yield ExitCode.Success
}

