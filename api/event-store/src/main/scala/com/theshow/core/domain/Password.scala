package com.theshow.core.domain

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class Password(
                          password: String
                        ) extends AnyVal

object Password {
  implicit val codec: Codec[Password] = deriveUnwrappedCodec[Password]
}