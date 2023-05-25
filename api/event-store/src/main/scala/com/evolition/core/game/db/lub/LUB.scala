package com.evolition.core.game.db.lub

import cats.effect.IO
import cats.implicits._
import com.evolition.core.game.db.config.Config
import com.evolition.core.http.routes.LobbyCustom
import doobie.implicits.toSqlInterpolator
import doobie.{HC, HPS}
import doobie.implicits._

final case class LUB(id: Int, user_id: Int, lobby_id: Int, bet_id: Option[Int])

object LUB {

  def getLUB(userId: Int): IO[Option[LUB]] = {
    val queryString = "SELECT * FROM lobbies_users_bets WHERE user_id = ?"
    HC.stream[LUB](
      queryString,
      HPS.set(userId),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def deleteLUB(id: Int) = {
    IO.println("delete lub") *>
    sql"DELETE FROM lobbies_users_bets WHERE id = $id"
      .update
      .run
      .transact(Config().xa).debug()
  }

  def createWithLobbyLUB(userId: Int, lobbyId: Int) = {
    sql"insert into lobbies_users_bets (user_id, lobby_id) values ($userId, $lobbyId)"
      .update.withUniqueGeneratedKeys[Int]("id")
      .transact(Config().xa).debug()
  }

  def updateBetLUB(id: Int, betId: Int) = {
    sql"UPDATE lobbies_users_bets SET bet_id = $betId WHERE id = $id"
      .update //Update0
      .run //ConnectionIO[Int]
      .transact(Config().xa).debug() //IO[Int]
  }

  def updateBetEqNull(userId: Int) = {
    sql"UPDATE lobbies_users_bets SET bet_id = null WHERE user_id = $userId"
      .update //Update0
      .run //ConnectionIO[Int]
      .transact(Config().xa).debug() //IO[Int]
  }
}
