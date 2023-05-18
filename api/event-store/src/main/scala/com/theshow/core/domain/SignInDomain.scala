package com.theshow.core.domain

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class SignInDomain(
                  userName: UserName,
                  password: Password,
                )

object SignInDomain {
  implicit val config: Configuration        = Configuration.default
  implicit val codec: Codec.AsObject[SignInDomain] = deriveConfiguredCodec[SignInDomain]
}