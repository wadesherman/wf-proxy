package com.casadeoso

import cats.effect._
import com.casadeoso.apikey.ApiKey
import com.casadeoso.cache.Cache
import com.casadeoso.request.BetterForecastv9
import com.casadeoso.response.LessBetterForecast
import com.casadeoso.token.Token
import com.casadeoso.transform.Transformer
import com.comcast.ip4s._
import com.typesafe.scalalogging.StrictLogging
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

object Main extends IOApp with StrictLogging {

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
  val tokenCache = new File(
    appConfig.configDir + "/token.json"
  )

  val client: Resource[IO, Client[IO]] = EmberClientBuilder
    .default[IO]
    .build

  implicit val forecastDecoder: EntityDecoder[IO, BetterForecastv9] =
    jsonOf[IO, BetterForecastv9]

  implicit val keyDecoder: EntityDecoder[IO, ApiKey] =
    jsonOf[IO, ApiKey]

  implicit val tokenDecoder: EntityDecoder[IO, Token] =
    jsonOf[IO, Token]

  def getApiKey: IO[Option[ApiKey]] =
    Cache
      .get[ApiKey](keyCache)
      .map(
        _.flatMap(k =>
          if (k.isActive) {
            Some(k)
          } else {
            logger.warn(f"API key expired.")
            None
          }
        )
      )

  def setApiKey(k: ApiKey): IO[Option[ApiKey]] = Cache.put(k, keyCache, 9999.days)

  def getToken: IO[Option[Token]] =
    Cache
      .get[Token](tokenCache)
      .map(
        _.flatMap(t =>
          if (t.isActive) {
            Some(t)
          } else {
            logger.warn(f"Token expired.")
            None
          }
        )
      )

  def setToken(t: Token): IO[Option[Token]] = Cache.put(t, tokenCache, 9999.days)

  def cachedForecast: IO[Option[BetterForecastv9]] =
    Cache.get[BetterForecastv9](forecastCache)

  def apiValue(maybeKey: Option[ApiKey], maybeToken: Option[Token]): IO[Either[AppErrors, BetterForecastv9]] = {
    (maybeKey, maybeToken) match {
      case (None, _) => IO(Left(NoApiKey()))
      case (_, None) => IO(Left(NoToken()))
      case (Some(apiKey), Some(token)) =>
        client.use { c =>
          c.get[Either[AppErrors, BetterForecastv9]](appConfig.getForecastEndpoint(apiKey.key, token.token)) {
            case Status.Successful(r) =>
              r.attemptAs[BetterForecastv9]
                .fold(
                  e => {
                    logger.warn(f"Json Parsing Error: ${e.message}")
                    Left(JsonParsingError())
                  },
                  r => Right(r)
                )
                .flatMap {
                  case Left(value) => IO(Left(value))
                  case Right(f) =>
                    Cache.put(f, forecastCache, appConfig.getForecastRefreshDuration).map {
                      case Some(f) => Right(f)
                      case None => {
                        logger.warn(f"Error writing forecast to cache")
                        Left(FileSystemError())
                      }
                    }
                }
            case Status.Unauthorized(_) => {
              logger.error(f"API Key or Token Unauthorized")
              (setApiKey(apiKey.copy(isActive = false)) *> setToken(token.copy(isActive = false)))
                .map(_ => Left(ApiKeyError()))
            }
            case e => {
              logger.error(f"Unhandled API error: ${e.status.code}")
              IO(Left(ApiError()))
            }
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
            case Some(key) => Ok(key.asJson)
            case None      => NoContent()
          }
      }
      case GET -> Root / "token" => {
        getToken
          .flatMap {
            case Some(token) => Ok(token.asJson)
            case None        => NoContent()
          }
      }
      case GET -> Root / "url" / endpoint => {
        (for {
          k <- getApiKey
          t <- getToken
        } yield (k, t))
          .flatMap {
            case (Some(key), Some(token)) =>
              endpoint match {
                case "forecast"    => Ok(appConfig.getForecastEndpoint(key.key, token.token))
                case "observation" => Ok(appConfig.getObservationEndpoint(key.key, token.token))
                case _             => NoContent()
              }
            case _ => NoContent()
          }
      }
      case GET -> Root / "forecast" => {
        val apiKey: IO[Option[ApiKey]] = getApiKey
        val token: IO[Option[Token]] = getToken
        val cachedResponse: IO[Option[BetterForecastv9]] = cachedForecast
        val apiResponse: IO[Either[AppErrors, BetterForecastv9]] = for {
          key <- apiKey
          t   <- token
          r   <- apiValue(key, t)
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
              case CacheMiss()        => NoContent()                   // 204
              case FileSystemError()  => InternalServerError()         // 500
              case JsonParsingError() => InternalServerError()         // 500
              case NoApiKey()         => ProxyAuthenticationRequired() // 407
              case NoToken()          => ProxyAuthenticationRequired() // 407
              case ApiKeyError()      => Forbidden()                   // 403
              case ApiError()         => ServiceUnavailable()          // 503
            }

          }
          case Right(forecast) => Ok(forecast.asJson)
        }
      }
      case POST -> Root / "key" / newKey => {
        setApiKey(ApiKey(newKey)).flatMap {
          case Some(r) => Ok(r.asJson)
          case None    => InternalServerError()

        }
      }
      case POST -> Root / "token" / newToken => {
        setToken(Token(newToken)).flatMap {
          case Some(r) => Ok(r.asJson)
          case None    => InternalServerError()
        }
      }
    }
    .orNotFound // 404

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
