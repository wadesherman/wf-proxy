package com.casadeoso.request

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}

@ConfiguredJsonCodec
case class Station(
    agl: Double,
    elevation: Double,
    includesTempest: Option[Boolean],
    isStationOnline: Boolean,
    // networkData: List[???]
    state: Int,
    stationId: Int
)
object Station {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
