package com.theshow.core.utils

import cats.effect.kernel.Async
import org.http4s.Request
import org.typelevel.ci.CIString

object Token {
  def checkToken[F[_]: Async](req: Request[F]) = req.headers.get(CIString("XXX-TOKEN")) match {
    case Some(value) => Some(value.head.value)
    case None => None
  }

  def tokenVerification = ???
}
