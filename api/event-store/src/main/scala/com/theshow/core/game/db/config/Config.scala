package com.theshow.core.game.db.config

import cats.effect.IO
import doobie.util.transactor.Transactor

final case class Config(xa: Transactor[IO])

object Config {
  def config() = Config(
    Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:gamedb",
    "docker",
    "docker"
  ))
}
