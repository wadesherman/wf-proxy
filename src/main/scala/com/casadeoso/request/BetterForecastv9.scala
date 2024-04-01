package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class BetterForecastv9(
    buildCode: String,
    client: Client,
    currentConditions: CurrentConditions,
    forecast: Forecast,
    latitude: Double,
    locationName: String,
    longitude: Double,
    refreshIntervalSeconds: Int,
    sourceId: Int,
    sourceIdConditions: Int,
    station: Station,
    status: Status,
    timezone: String,
    timezoneOffsetMinutes: Int,
    units: Units,
    unitsDisplay: Units
)

object BetterForecastv9 {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
