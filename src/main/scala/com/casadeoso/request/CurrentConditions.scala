package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class CurrentConditions(
    airDensity: Double,
    airTemperature: Double,
    brightness: Long,
    conditions: String,
    deltaT: Double,
    dewPoint: Double,
    feelsLike: Double,
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
    wetBulbGlobeTemperature: Double,
    wetBulbTemperature: Double,
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
