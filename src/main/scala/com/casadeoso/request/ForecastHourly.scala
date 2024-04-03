package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class ForecastHourly(
    airTemperature: Double,
    conditions: String,
    feelsLike: Double,
    icon: String,
    localDay: Int,
    localHour: Int,
    precip: Double,
    precipIcon: String,
    precipProbability: Int,
    precipType: String,
    relativeHumidity: Int,
    seaLevelPressure: Double,
    time: Long,
    uv: Double,
    windAvg: Double,
    windAvgColor: String,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Double,
    windGustColor: String
)
object ForecastHourly {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
