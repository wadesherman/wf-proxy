package com.casadeoso.token

import io.circe.generic.JsonCodec

@JsonCodec
case class Token(
    token: String,
    isActive: Boolean = true
)
