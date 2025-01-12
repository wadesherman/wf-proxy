package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class CurrentConditions(
    airDensity: Option[Double],
    airTemperature: Double,
    brightness: Option[Long],
    conditions: String,
    deltaT: Option[Double],
    dewPoint: Option[Double],
    feelsLike: Double,
    icon: String,
    includesStationData: Boolean,
    isPrecipLocalDayRainCheck: Option[Boolean],
    isPrecipLocalYesterdayRainCheck: Option[Boolean],
    lightningStrikeCountLast_1hr: Option[Int],
    lightningStrikeCountLast_3hr: Option[Int],
    lightningStrikeLastDistance: Option[Int],
    lightningStrikeLastDistanceMsg: Option[String],
    lightningStrikeLastEpoch: Option[Long],
    precipAccumLocalDay: Option[Double],
    precipAccumLocalYesterday: Option[Double],
    precipMinutesLocalDay: Option[Int],
    precipMinutesLocalYesterday: Option[Int],
    pressureTrend: Option[String],
    relativeHumidity: Int,
    seaLevelPressure: Option[Double],
    solarRadiation: Option[Int],
    stationPressure: Option[Double],
    time: Option[Long],
    uv: Option[Int],
    wetBulbGlobeTemperature: Option[Double],
    wetBulbTemperature: Option[Double],
    windAvg: Double,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Double
)

object CurrentConditions {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
