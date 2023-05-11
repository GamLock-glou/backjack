package com.theshow.core.server

import cats.effect.ExitCode
import cats.effect.kernel.Async
import cats.effect.std.Console
import com.theshow.core.config.Config
import com.theshow.core.http.routes.EventRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig}
import fs2.Stream
import org.http4s.Method
import cats.effect._

object Server {
  def stream[F[_]: Async: Console](config: Config): fs2.Stream[F, ExitCode] = for {
    _ <- Stream.eval(Console[F].println("Starting the server"))
//    kafkaProducerAlgebra: KafkaProducerAlgebra[F] = KafkaProducerAlgebra
//      .impl[F](config.kafkaConfig)
//    kafkaConsumerAlgebra = KafkaConsumerAlgebra
//      .impl[F](config.kafkaConfig)
    corService = CORS(
      EventRoutes[F].GetHttpApp(),
      CORSConfig.default.withAllowedOrigins(Set("http://localhost:3000"))
        .withAllowedMethods(Some(Set(Method.POST)))
    )
    sever <-  BlazeServerBuilder[F]
      .bindHttp(
        config.serverConfig.port.value,
        config.serverConfig.host.value
      )
      .withHttpApp(EventRoutes[F].GetHttpApp())
      .serve
//      .concurrently(kafkaConsumerAlgebra.consume)
  } yield sever
}
