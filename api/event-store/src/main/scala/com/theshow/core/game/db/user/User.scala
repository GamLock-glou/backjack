package com.theshow.core.game.db.user

import cats.Functor
import cats.effect.IO
import cats.effect.kernel.Async
import cats.implicits._
import com.theshow.core.game.db.config.Config
import com.theshow.core.http.routes.UserRequest
import doobie.{HC, HPS}
import doobie.implicits._

final case class User (id: Number, name: String, password: String, money: Double, token: String)

object User {
  def getUserByToken(token: String): IO[Option[UserRequest]] = {
    val queryString = "select id, name, money, token from users where token = ?"
    IO.println("get user") *> HC.stream[UserRequest](
      queryString,
      HPS.set(token),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }
  def getUser(name: String, password: String): IO[Option[UserRequest]] = {
    val queryString = "select id, name, money, token from users where name = ? and password = ?"
    HC.stream[UserRequest](
      queryString,
      HPS.set((name, password)),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug()
  }

  def updateUserMoney(id: Int, bet: Double): IO[Option[UserRequest]] = {
    IO.println("update user money") *>
    sql"UPDATE users SET money = money + $bet WHERE id = $id"
      .update //Update0
      .withUniqueGeneratedKeys[UserRequest]("id", "name", "money", "token")
      .transact(Config().xa).debug() //IO[Int]
      .option
  }

}