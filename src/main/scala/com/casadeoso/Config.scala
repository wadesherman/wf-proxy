package com.casadeoso

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class Config(
    configDir: String,
    stationId: Int,
    forecastEndpoint: String,
    observationEndpoint: String,
    forecastRefreshMin: Int
) {
  val getForecastEndpoint: String => String    = k => forecastEndpoint + k
  val getObservationEndpoint: String => String = k => observationEndpoint + k
  val getForecastRefreshDuration: FiniteDuration = forecastRefreshMin.minutes
}
