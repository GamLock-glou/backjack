package com.theshow.core.game.db.lobby

import cats.effect.IO
import doobie.{HC, HPS}

object Lobby {

  def getLobby(name: String, password: String): IO[Option[LobbiesCustom]] = {
    val queryString = "select id, name, money, token from users where name = ? and password = ?"
    HC.stream[LobbiesCustom](
      queryString,
      HPS.set((name, password)),
      100
    ).compile.toList.map(_.headOption).transact(Config.config.xa).debug()
  }

}
