package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Forecast(
    daily: List[ForecastDaily],
    hourly: List[ForecastHourly]
)
object Forecast {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
