package com.theshow.core.config

import cats.effect.kernel.Async
import ciris.{ConfigValue, env}
import com.theshow.core.domain.{Host, Port}
import cats.implicits._

final case class ServerConfig(
                             port: Port,
                             host: Host
                             )

object ServerConfig {
  def serverConfig[F[_]: Async]: ConfigValue[F, ServerConfig] = (
    env("PORT").as[Int].default(4000),
    env("HOST").default("localhost")
  ).parMapN((port, host) => ServerConfig(Port(port), Host(host)))
}
