package com.casadeoso

case class Config(
    configDir: String,
    stationId: Int,
    forecastEndpoint: String,
    observationEndpoint: String
) {
  val getForecastEndpoint: String => String    = k => forecastEndpoint + k
  val getObservationEndpoint: String => String = k => observationEndpoint + k

}
