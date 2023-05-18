package com.theshow.core.config

import cats.effect.kernel.Async
import ciris.ConfigValue
import cats.implicits._

final case class Config(
                         serverConfig: ServerConfig,
                       )
object Config {
  def config[F[_]: Async]: ConfigValue[F, Config] = {
    //parMapN
    (ServerConfig.serverConfig[F])
      .map((serverConfig) => Config(serverConfig))
  }
}
