package com.casadeoso

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import pureconfig._
import pureconfig.generic.auto._

class ConfigSpec extends AnyFlatSpec with EitherValues {
  it should "load a config" in {
    val appConfig = ConfigSource.default.load[Config]

    appConfig.value shouldBe Config(
      "/tmp/weatherflow-proxy/",
      123456,
      "https://swd.weatherflow.com/swd/rest/better_forecast?station_id=123456&units_temp=f&units_wind=mph&units_pressure=inhg&units_precip=in&units_distance=mi&api_key=",
      "https://swd.weatherflow.com/swd/rest/observations/stn/123456?units_temp=f&units_wind=mph&units_pressure=inhg&units_precip=in&units_distance=mi&api_key=",
      29
    )
  }
}
