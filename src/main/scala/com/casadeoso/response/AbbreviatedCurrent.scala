package com.casadeoso.response

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class AbbreviatedCurrent(
    airTemperature: Int,
    feelsLike: Int,
    icon: String,
    precipAccumLocalDay: Double,
    precipAccumLocalYesterday: Double,
    pressureTrend: String,
    relativeHumidity: Int,
    windAvg: Int,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Int
)

object AbbreviatedCurrent {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
