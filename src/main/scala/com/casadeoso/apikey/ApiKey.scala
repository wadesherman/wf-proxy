package com.casadeoso.apikey

import io.circe.generic.JsonCodec

@JsonCodec
case class ApiKey(
    key: String,
    isActive: Boolean = true
)
