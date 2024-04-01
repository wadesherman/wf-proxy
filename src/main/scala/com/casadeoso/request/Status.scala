package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Status(
    statusCode: Int,
    statusMessage: String
)

object Status {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
