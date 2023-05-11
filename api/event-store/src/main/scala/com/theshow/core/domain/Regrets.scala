package com.theshow.core.domain

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class Regrets(regrets: String) extends AnyVal

object Regrets {
  implicit val codec: Codec[Regrets] = deriveUnwrappedCodec[Regrets]
}
