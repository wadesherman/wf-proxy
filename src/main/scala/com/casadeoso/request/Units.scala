package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Units(
    unitsAirDensity: Option[String],
    unitsBrightness: Option[String],
    unitsDistance: Option[String],
    unitsOther: Option[String],
    unitsPrecip: Option[String],
    unitsPressure: Option[String],
    unitsSolarRadiation: Option[String],
    unitsTemp: Option[String],
    unitsWind: Option[String]
)
object Units {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
