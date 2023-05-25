package com.evolition.core.game.db.config

import cats.effect.IO
import cats.effect.kernel.Async
import doobie.util.transactor.Transactor

final case class Config(xa: Transactor[IO])

object Config {
  def apply(): Config = Config(
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql:gamedb",
      "docker",
      "docker"
    ))

}
