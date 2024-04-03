package com.casadeoso.response

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class AbbreviatedForecast(
    airTempHigh: Double,
    airTempLow: Double,
    icon: String,
    precip: Double,
    precipIcon: String,
    precipProbability: Int,
    precipType: String,
    relativeHumidity: Int,
    windAvg: Double,
    day: Int,
    month: Int
)
object AbbreviatedForecast {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
