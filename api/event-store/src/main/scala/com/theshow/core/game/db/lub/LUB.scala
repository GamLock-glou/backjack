package com.theshow.core.game.db.lub

import cats.effect.IO
import cats.implicits._
import com.theshow.core.game.db.config.Config
import com.theshow.core.http.routes.LobbyCustom
import doobie.implicits.toSqlInterpolator
import doobie.{HC, HPS}
import doobie.implicits._

final case class LUB(id: Int, user_id: Int, lobby_id: Int, bet_id: Option[Int])

object LUB {

  def checkUserInLUB(user_id: Int): IO[Option[LUB]] = {
    val queryString = "SELECT * FROM lobbies_users_bets WHERE user_id = ?"
    HC.stream[LUB](
      queryString,
      HPS.set(user_id),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def checkUserInLobbyAndLUB(user_id: Int): IO[Option[(LobbyCustom, LUB)]] = {
    val queryString = "select * from lobbies l join lobbies_users_bets lub on l.id=lub.lobby_id where lub.user_id = ?"
    HC.stream[(LobbyCustom, LUB)](
      queryString,
      HPS.set(user_id),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def deleteLUB(id: Int) = {
    IO.println("delete") *>
    sql"DELETE FROM lobbies_users_bets WHERE id = $id"
      .update
      .run
      .transact(Config().xa).debug()
  }

  def createWithLobbyLUB(user_id: Int, lobby_id: Int, bet_id: Option[Int]) = {
    sql"insert into lobbies_users_bets (user_id, lobby_id, bet_id) values ($user_id, $lobby_id, ${bet_id})"
      .update.withUniqueGeneratedKeys[Int]("id")
      .transact(Config().xa).debug()
  }

  def updateBetLUB(id: Int, bet_id: Int) = {
    sql"UPDATE lobbies_users_bets SET bet_id = $bet_id WHERE id = $id"
      .update //Update0
      .run //ConnectionIO[Int]
      .transact(Config().xa).debug() //IO[Int]
  }
}
