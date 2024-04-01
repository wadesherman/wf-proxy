package com.casadeoso.cache

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

import java.time.Instant

@ConfiguredJsonCodec
case class Cached[T](
    expires: Instant,
    value: T
)

object Cached {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
