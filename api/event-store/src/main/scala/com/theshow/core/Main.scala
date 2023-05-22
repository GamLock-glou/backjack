package com.theshow.core

import cats.effect.std.Queue
import cats.effect.{Async, ExitCode, IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server._
import com.theshow.core.http.routes.EventRoutes
import org.http4s.Method
import org.http4s.server.middleware.{CORS, ErrorHandling}
import cats.implicits._
import org.http4s.server.websocket.WebSocketBuilder2

object Main extends IOApp {
  private val eventRouters = EventRoutes.apply();
//  private val httpApp = ErrorHandling {
//    Seq(
//      eventRouters.signInRoutes,
//      eventRouters.lobbyRoutes,
//      eventRouters.betsRouters
//    ).reduce(_ <+> _)
//  }.orNotFound
  private def httpApp(wsBuilder: WebSocketBuilder2[IO]) = ErrorHandling {
    Seq(
      eventRouters.signInRoutes,
      eventRouters.lobbyRoutes,
      eventRouters.betsRouters
//      eventRouters.wsRoutes(wsBuilder)
    ).reduce(_ <+> _)
  }.orNotFound

  def buildApp(wsBuilder: WebSocketBuilder2[IO]) = CORS.policy
    .withAllowOriginAll
    .withAllowMethodsIn(Set(Method.GET, Method.POST))
    .withAllowCredentials(false)
    .apply(httpApp(wsBuilder))
  
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- Async[IO].delay(println("Starting the server"))
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"127.0.0.1")
        .withPort(port"4000")
        .withHttpWebSocketApp(wsBuilder => buildApp(wsBuilder))
        .build
        .useForever
    } yield ExitCode.Success
}

