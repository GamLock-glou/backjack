package com.evolition.core.game.db.lobby

import cats.effect.IO
import cats.implicits._
import com.evolition.core.game.db.config.Config
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.http.routes.LobbyCustom
import doobie.implicits.toSqlInterpolator
import doobie.{HC, HPS}
import doobie.implicits._


object Lobby {
  def getLobbies(): IO[List[LobbyCustom]] = {
    val query = sql"SELECT id, room_name, limit_users, users_count FROM lobbies".query[LobbyCustom]
    val action = query.to[List]
    action.transact(Config().xa).debug()
  }

  def getLobby(lobby_id: Int): IO[Option[LobbyCustom]] = {
    val queryString = "SELECT * FROM lobbies WHERE id = ?"
    HC.stream[LobbyCustom](
      queryString,
      HPS.set(lobby_id),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def updateLobby(lobby_id: Int, value: Int = 1) = {
    sql"UPDATE lobbies SET users_count = users_count + $value WHERE id = $lobby_id"
      .update //Update0
      .run //ConnectionIO[Int]
      .transact(Config().xa).debug()
  }

}
