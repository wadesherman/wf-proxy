package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Client(
    baseUrl: String
)
object Client {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames

}
