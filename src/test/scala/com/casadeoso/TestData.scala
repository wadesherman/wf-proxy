package com.casadeoso

import io.circe.generic.JsonCodec

@JsonCodec
case class TestData(
    aString: String,
    anInt: Int,
    aBool: Boolean,
    anOption: Option[String]
)
