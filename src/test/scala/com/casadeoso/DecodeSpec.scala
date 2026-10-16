package com.casadeoso

import io.circe.parser.decode
import com.casadeoso.request.BetterForecastv9
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec

class DecodeSpec extends AnyFlatSpec with OptionValues {

  it should "decode response" in {
    val string = scala.io.Source.fromResource("response.json").mkString
    val foo = decode[BetterForecastv9](string)
    println(foo)

  }
}
