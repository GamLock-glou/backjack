package com.theshow.core.websocket.routes

import cats.effect.kernel.Async
import org.http4s.dsl.Http4sDsl

import cats.effect.std.Queue
import cats.syntax.all._
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import org.http4s.{HttpRoutes, _}


case class EventWebSocketRoutes[F[_]: Async]()
  extends Http4sDsl[F]{
  private def echoRoute(wsb: WebSocketBuilder2[F]) = HttpRoutes.of[F] {

    // websocat "ws://localhost:9002/echo"
    case GET -> Root / "echo" =>
      println("echo")
      // Pipe is a stream transformation function of type `Stream[F, I] => Stream[F, O]`. In this case
      // `I == O == WebSocketFrame`. So the pipe transforms incoming WebSocket messages from the client to
      // outgoing WebSocket messages to send to the client.
      val echoPipe: Pipe[F, WebSocketFrame, WebSocketFrame] =
        _.collect { case WebSocketFrame.Text(message, _) =>
          WebSocketFrame.Text(message)
        }
      println(echoPipe.toString())

      for {
        // Unbounded queue to store WebSocket messages from the client, which are pending to be processed.
        // For production use bounded queue seems a better choice. Unbounded queue may result in out of
        // memory error, if the client is sending messages quicker than the server can process them.
        queue <- Queue.unbounded[F, WebSocketFrame]
        response <- wsb.build(
          // Sink, where the incoming WebSocket messages from the client are pushed to.
          receive = _.evalMap(queue.offer),
          // Outgoing stream of WebSocket messages to send to the client.
          send = Stream.repeatEval(queue.take).through(echoPipe),
        )
      } yield response

    // Exercise 1. Change the echo route to respond with the current time, when the client sends "time". Allow
    // whitespace characters before and after the command, so " time " should also be considered valid. Note
    // that getting the current time is a side effect.

    // Exercise 2. Change the echo route to notify the client every 5 seconds how long it is connected.
    // Tip: you can merge streams via `merge` operator.
  }

  // Topics provide an implementation of the publish-subscribe pattern with an arbitrary number of
  // publishers and an arbitrary number of subscribers.
  private def chatRoute(chatTopic: Topic[F, String])(wsb: WebSocketBuilder2[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    // websocat "ws://localhost:9002/chat"
    case GET -> Root / "chat" =>
      wsb.build(
        // Sink, where the incoming WebSocket messages from the client are pushed to.
        receive = chatTopic.publish.compose[Stream[F, WebSocketFrame]](_.collect {
          case WebSocketFrame.Text(message, _) => message
        }),
        // Outgoing stream of WebSocket messages to send to the client.
        send = chatTopic.subscribe(maxQueued = 10).map(WebSocketFrame.Text(_)),
      )

    // Exercise 3. Change the chat route to use the first message from a client as its username and prepend
    // it to every follow-up message. Tip: you will likely need to use fs2.Pull.
  }

  def httpApp(chatTopic: Topic[F, String])(wsb: WebSocketBuilder2[F]): HttpApp[F] = {
    echoRoute(wsb) <+> chatRoute(chatTopic)(wsb)
  }.orNotFound
}
