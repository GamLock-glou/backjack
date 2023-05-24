package com.theshow.core

import cats.effect.kernel.Ref
import cats.effect.std.Queue
import cats.effect.{Async, ExitCode, IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server._
import com.theshow.core.http.routes.EventRoutes
import org.http4s.Method
import org.http4s.server.middleware.{CORS, ErrorHandling}
import cats.implicits._
import com.theshow.core.game.logic.Game
import org.http4s.server.websocket.WebSocketBuilder2

object Main extends IOApp {
  private val eventRouters = EventRoutes.apply();
  private def httpApp(wsBuilder: WebSocketBuilder2[IO],  ref: Ref[IO, List[Game]]) = ErrorHandling {
    Seq(
      eventRouters.signInRoutes,
      eventRouters.lobbyRoutes,
      eventRouters.betsRouters,
      eventRouters.gameRouters(ref)
//      eventRouters.wsRoutes(wsBuilder)
    ).reduce(_ <+> _)
  }.orNotFound

  private def buildApp(wsBuilder: WebSocketBuilder2[IO], ref: Ref[IO, List[Game]]) = CORS.policy
    .withAllowOriginAll
    .withAllowMethodsIn(Set(Method.GET, Method.POST))
    .withAllowCredentials(false)
    .apply(httpApp(wsBuilder, ref))


  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- Async[IO].delay(println("Starting the server"))
      ref <- Ref[IO].of(List.empty[Game])
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"127.0.0.1")
        .withPort(port"4000")
        .withHttpWebSocketApp(wsBuilder => buildApp(wsBuilder, ref))
        .build
        .useForever
    } yield ExitCode.Success
}

