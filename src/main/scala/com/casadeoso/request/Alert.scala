package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Alert (
    event: String,
    id: String,
    priority: Int
                 )

object Alert {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}