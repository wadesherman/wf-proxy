package com.casadeoso

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class Config(
    configDir: String,
    stationId: Int,
    forecastEndpoint: String,
    observationEndpoint: String,
    forecastRefreshMin: Int
) {
  val getForecastEndpoint: (String, String) => String    = (k, t) => forecastEndpoint + k + "&token=" + t
  val getObservationEndpoint: (String, String) => String = (k, t) => observationEndpoint + k + "&token=" + t
  val getForecastRefreshDuration: FiniteDuration = forecastRefreshMin.minutes
}
