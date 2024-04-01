package com.casadeoso.request

case class Observation(
    stationId: Int,
    `type`: String,
    obFields: Seq[String],
    obs: Seq[Seq[Double]],
    status: Status,
    source: String,
    units: Units
)
