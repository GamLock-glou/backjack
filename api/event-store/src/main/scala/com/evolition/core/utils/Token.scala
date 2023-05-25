package com.evolition.core.utils

import cats.data.NonEmptyList
import cats.effect.IO
import cats.effect.kernel.Async
import com.evolition.core.game.db.user.User
import org.http4s.{Header, Request}
import org.typelevel.ci.CIString

final case class HeaderToken(value: Option[NonEmptyList[Header.Raw]])

object HeaderToken {
  def apply(req: Request[IO]): HeaderToken = HeaderToken(req.headers.get(CIString("XXX-TOKEN")))
  def getToken(token: HeaderToken): Option[String] = token.value match {
    case Some(value) => {
      println(value.head.value)
      Some(value.head.value)
    }
    case None => None
  }
}
