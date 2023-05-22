package com.theshow.core.game.db.bets

import cats.effect.IO
import cats.implicits._
import com.theshow.core.game.db.config.Config
import com.theshow.core.game.db.lub.LUB
import doobie.implicits.toSqlInterpolator
import doobie.{HC, HPS}
import doobie.implicits._

final case class Bet(id: Int, bet: Double, is_win: Option[Boolean])


object Bet {
  def addBet(user_id: Int, lobby_id: Int, bet: Double) = {
    sql"insert into bets (user_id, lobby_id, bet) values ($user_id, $lobby_id, $bet)"
      .update
      .withUniqueGeneratedKeys[Bet]("id", "bet", "is_win")
      .transact(Config().xa).debug()
  }
  def getBet(id: Int) = {
    val queryString = "SELECT id, bet, is_win FROM bets where id = ?"
    HC.stream[Bet](
      queryString,
      HPS.set(id),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def placeABet(user_id: Int, lobby_id: Int, bet: Double) = {
    for {
      lub <- LUB.checkUserInLUB(user_id)
      resp <- lub match {
        case Some(v) => for {
          b <- addBet(user_id, lobby_id, bet)
          _ <- LUB.updateBetLUB(v.id, b.id)
        } yield Some(b)
        // TODO: a bug may occur
        case None => IO(None)
      }
    } yield resp
  }

}