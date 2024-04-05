package com.casadeoso.transform

import com.casadeoso.{AppErrors, JsonParsingError}
import com.casadeoso.request.{BetterForecastv9, CurrentConditions, ForecastDaily}
import com.casadeoso.response.{AbbreviatedCurrent, AbbreviatedForecast, LessBetterForecast}

object Transformer {

  def ccIconToIcon(icon: String): String = icon.stripPrefix("cc-")

  def abbreviateDaily(d: ForecastDaily): AbbreviatedForecast =
    AbbreviatedForecast(
      airTempHigh = d.airTempHigh,
      airTempLow = d.airTempLow,
      icon = d.icon,
      precip = d.precip,
      precipIcon = d.precipIcon,
      precipProbability = d.precipProbability,
      precipType = d.precipType,
      relativeHumidity = d.relativeHumidity,
      windAvg = d.windAvg,
      day = d.dayNum,
      month = d.monthNum
    )

  def abbreviateCurrent(c: CurrentConditions): AbbreviatedCurrent = {
    AbbreviatedCurrent(
      airTemperature = c.airTemperature,
      feelsLike = c.feelsLike,
      icon = ccIconToIcon(c.icon),
      precipAccumLocalDay = c.precipAccumLocalDay,
      precipAccumLocalYesterday = c.precipAccumLocalYesterday,
      pressureTrend = c.pressureTrend,
      relativeHumidity = c.relativeHumidity,
      windAvg = c.windAvg,
      windDirection = c.windDirection,
      windDirectionCardinal = c.windDirectionCardinal,
      windDirectionIcon = c.windDirectionIcon,
      windGust = c.windGust
    )
  }

  def abbreviate(in: BetterForecastv9): Either[AppErrors, LessBetterForecast] = {
    in.forecast.daily.map(abbreviateDaily) match {
      case today :: tomorrow :: _ =>
        Right(
          LessBetterForecast(
            currentConditions = abbreviateCurrent(in.currentConditions),
            forecast = Nil,
            today = today,
            tomorrow = tomorrow,
            timestamp = in.currentConditions.time
          )
        )
      case _ => Left(JsonParsingError())
    }
  }
}
