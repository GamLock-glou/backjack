//package com.theshow.core.server
//
//import cats.effect.ExitCode
//import cats.effect.kernel.Async
//import cats.effect.std.Console
//import com.theshow.core.config.Config
//import com.theshow.core.http.routes.EventRoutes
//import org.http4s.server.middleware.{CORS, CORSConfig}
//import fs2.Stream
//import fs2.concurrent.Topic
//import org.http4s.Method
//import org.http4s.blaze.server.BlazeServerBuilder
//
//object Server {
//  case class ToClient(message: String)
//  def stream[F[_]: Async: Console](config: Config): fs2.Stream[F, ExitCode] = for {
//    _ <- Stream.eval(Console[F].println("Starting the server"))
//    corService = CORS(
//      EventRoutes[F].GetHttpApp(),
//      CORSConfig.default.withAllowedOrigins(Set("http://localhost:3000"))
//        .withAllowedMethods(Some(Set(Method.POST, Method.GET, Method.PUT, Method.DELETE)))
//    )
//    t <- Topic[F, ToClient](ToClient("==="));
//
//    sever <-  BlazeServerBuilder[F]
//      .bindHttp(
//        config.serverConfig.port.value,
//        config.serverConfig.host.value
//      )
//      .withHttpApp(corService)
//      .withHttpWebSocketApp(wsb => EventRoutes[F].httpWebSocket(chatTopic))
//      .serve
//  } yield sever
//}
