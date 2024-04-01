package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class ForecastDaily(
    airTempHigh: Int,
    airTempHighColor: String,
    airTempLow: Int,
    airTempLowColor: String,
    conditions: String,
    dayNum: Int,
    dayStartLocal: Long,
    icon: String,
    monthNum: Int,
    precip: Double,
    precipIcon: String,
    precipProbability: Int,
    precipType: String,
    relativeHumidity: Int,
    relativeSunshineDuration: Int,
    sunrise: Long,
    sunset: Long,
    windAvg: Double
)
object ForecastDaily {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
