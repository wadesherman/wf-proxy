package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class CurrentConditions(
    airDensity: Double,
    airTemperature: Int,
    brightness: Long,
    conditions: String,
    deltaT: Int,
    dewPoint: Int,
    feelsLike: Int,
    icon: String,
    includesStationData: Boolean,
    isPrecipLocalDayRainCheck: Boolean,
    isPrecipLocalYesterdayRainCheck: Boolean,
    lightningStrikeCountLast_1hr: Int,
    lightningStrikeCountLast_3hr: Int,
    lightningStrikeLastDistance: Int,
    lightningStrikeLastDistanceMsg: String,
    lightningStrikeLastEpoch: Long,
    precipAccumLocalDay: Double,
    precipAccumLocalYesterday: Double,
    precipMinutesLocalDay: Int,
    precipMinutesLocalYesterday: Int,
    pressureTrend: String,
    relativeHumidity: Int,
    seaLevelPressure: Double,
    solarRadiation: Int,
    stationPressure: Double,
    time: Long,
    uv: Int,
    wetBulbGlobeTemperature: Int,
    wetBulbTemperature: Int,
    windAvg: Int,
    windDirection: Int,
    windDirectionCardinal: String,
    windDirectionIcon: String,
    windGust: Int
)

object CurrentConditions {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
