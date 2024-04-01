package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class ForecastHourly(
    airTemperature: Int,
    conditions: String,
    feelsLike: Int,
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
    uv: Int,
    windAvg: Int,
    windAvgColor: String,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Int,
    windGustColor: String
)
object ForecastHourly {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
