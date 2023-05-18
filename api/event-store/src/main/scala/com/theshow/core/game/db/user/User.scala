package com.theshow.core.game.db.user

import cats.effect.IO
import cats.effect.kernel.Async
import cats.implicits._
import com.theshow.core.game.db.config.Config
import doobie.implicits._

final case class User (id: Number, name: String, password: String, money: Double, token: String)

object User {
  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] ${a}")
      a
    }
  }

  def findAllUsers: IO[List[String]] = {
    val query = sql"select name from user".query[String]
    val action = query.to[List]
    action.transact(Config.config.xa)
  }
}