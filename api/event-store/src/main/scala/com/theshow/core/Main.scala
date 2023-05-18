package com.theshow.core

import cats.effect.std.Queue
import cats.effect.{Async, ExitCode, IO, IOApp, Ref}
import com.comcast.ip4s._
import org.http4s.ember.server._
import com.theshow.core.http.routes.EventRoutes
import com.theshow.core.websocket.routes.EventWebSocketRoutes
import fs2.concurrent.Topic
import org.http4s.Method
import org.http4s.server.middleware.CORS
import org.http4s.websocket.WebSocketFrame
import fs2.Stream



object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- Async[IO].delay(println("Starting the server"))
      corsMethodSvc = CORS.policy
        .withAllowOriginAll
        .withAllowMethodsIn(Set(Method.GET, Method.POST))
        .withAllowCredentials(false)
        .apply(EventRoutes[IO].getHttpApp())
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"127.0.0.1")
        .withPort(port"4000")
        .withHttpApp(corsMethodSvc)
        .build
        .useForever
    } yield ExitCode.Success
}

