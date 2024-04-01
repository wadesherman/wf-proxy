package com.casadeoso.response

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class LessBetterForecast(
    currentConditions: AbbreviatedCurrent,
    forecast: Seq[AbbreviatedForecast],
    today: AbbreviatedForecast,
    tomorrow: AbbreviatedForecast
)

object LessBetterForecast {
  implicit val config: Configuration =
    Configuration.default.withSnakeCaseMemberNames
}
