package com.casadeoso.response

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class AbbreviatedCurrent(
    airTemperature: Double,
    feelsLike: Double,
    icon: String,
    precipAccumLocalDay: Double,
    precipAccumLocalYesterday: Double,
    relativeHumidity: Int,
    windAvg: Double,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Double
)

object AbbreviatedCurrent {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
