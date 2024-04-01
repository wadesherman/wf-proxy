package com.casadeoso

import cats.effect._
import com.casadeoso.apikey.ApiKey
import com.casadeoso.cache.Cache
import com.casadeoso.request.BetterForecastv9
import com.casadeoso.response.LessBetterForecast
import com.casadeoso.transform.Transformer
import com.comcast.ip4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpRoutes, Status}
import pureconfig._
import pureconfig.generic.auto._

import java.io.File
import scala.concurrent.duration.DurationInt

object Main extends IOApp {

  import io.circe.syntax._

  val appConfig: Config = ConfigSource.default.loadOrThrow[Config]

  val observationCache = new File(
    appConfig.configDir + "/observation.json"
  )
  val forecastCache = new File(
    appConfig.configDir + "/forecast.json"
  )
  val keyCache = new File(
    appConfig.configDir + "/key.json"
  )

  val client: Resource[IO, Client[IO]] = EmberClientBuilder
    .default[IO]
    .build

  implicit val forecastDecoder: EntityDecoder[IO, BetterForecastv9] =
    jsonOf[IO, BetterForecastv9]

  implicit val keyDecoder: EntityDecoder[IO, ApiKey] =
    jsonOf[IO, ApiKey]

  def getApiKey: IO[Option[ApiKey]] =
    Cache
      .get[ApiKey](keyCache)
      .map(
        _.flatMap(k =>
          if (k.isActive) {
            Some(k)
          } else {
            None
          }
        )
      )

  def setApiKey(k: ApiKey): IO[Option[ApiKey]] = Cache.put(k, keyCache, 9999.days)

  def cachedForecast: IO[Option[BetterForecastv9]] =
    Cache.get[BetterForecastv9](forecastCache)

  def apiValue(maybeKey: Option[ApiKey]): IO[Either[AppErrors, BetterForecastv9]] = {
    maybeKey match {
      case None => IO(Left(NoApiKey()))
      case Some(apiKey) =>
        client.use { c =>
          c.get[Either[AppErrors, BetterForecastv9]](appConfig.getForecastEndpoint(apiKey.key)) {
            case Status.Successful(r) =>
              r.attemptAs[BetterForecastv9]
                .fold(
                  _ => Left(JsonParsingError()),
                  r => Right(r)
                )
                .flatMap {
                  case Left(value) => IO(Left(value))
                  case Right(f) =>
                    Cache.put(f, forecastCache, 59.minutes).map {
                      case Some(f) => Right(f)
                      case None    => Left(FileSystemError())
                    }
                }
            case Status.Unauthorized(_) => {
              setApiKey(apiKey.copy(isActive = false)).map(_ => Left(ApiKeyError()))
            }
            case _ => IO(Left(ApiError()))
          }
        }
    }

  }

  val endpoints = HttpRoutes
    .of[IO] {
      case GET -> Root / "healthcheck" => Ok()
      case GET -> Root / "key" => {
        getApiKey
          .flatMap {
            case Some(key) => Ok(key.asJson.noSpaces)
            case None      => NoContent()
          }
      }
      case GET -> Root / "url" / endpoint => {
        getApiKey
          .flatMap {
            case Some(key) =>
              endpoint match {
                case "forecast"    => Ok(appConfig.getForecastEndpoint(key.key))
                case "observation" => Ok(appConfig.getObservationEndpoint(key.key))
                case _             => NoContent()
              }
            case None => NoContent()
          }
      }
      case GET -> Root / "forecast" => {
        val apiKey                                       = getApiKey
        val cachedResponse: IO[Option[BetterForecastv9]] = cachedForecast
        val apiResponse: IO[Either[AppErrors, BetterForecastv9]] = for {
          key <- apiKey
          r   <- apiValue(key)
        } yield r

        val io: IO[Either[AppErrors, LessBetterForecast]] = cachedResponse
          .map {
            case Some(value) => Right(value)
            case _           => Left(CacheMiss())
          }
          .flatMap {
            case Left(_) => apiResponse
            case r       => IO(r)
          }
          .map {
            case Left(l)  => Left(l)
            case Right(f) => Transformer.abbreviate(f)
          }

        io.flatMap {
          case Left(err) => {
            err match {
              case CacheMiss()        => NoContent()
              case FileSystemError()  => InternalServerError()
              case JsonParsingError() => InternalServerError()
              case NoApiKey()         => ProxyAuthenticationRequired()
              case ApiKeyError()      => Forbidden()
              case ApiError()         => ServiceUnavailable()
            }

          }
          case Right(forecast) => Ok(forecast.asJson.noSpaces)
        }
      }
      case POST -> Root / "key" / newKey => {
        setApiKey(ApiKey(newKey)).flatMap {
          case Some(r) => Ok(r.asJson.noSpaces)
          case None    => InternalServerError()

        }
      }
    }
    .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(endpoints)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
