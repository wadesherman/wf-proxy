package com.casadeoso.cache

import cats.effect.{IO, Resource}
import com.typesafe.scalalogging.StrictLogging

import java.io._
import java.time.Instant
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.jdk.DurationConverters.ScalaDurationOps

object Cache extends StrictLogging {

  import io.circe._
  import io.circe.parser._
  import io.circe.syntax._

  private def inputStream(
      f: File
  ): Resource[IO, Either[Throwable, FileInputStream]] =
    Resource.make {
      IO.blocking(new FileInputStream(f)).attempt // build
    } { inStream =>
      IO.blocking(inStream.map(_.close()))
    }

  private def outputStream(f: File): Resource[IO, Either[Throwable, FileOutputStream]] =
    Resource.make {
      IO.blocking({
        f.createNewFile()
        new FileOutputStream(f)
      }).attempt // build
    } { outStream =>
      IO.blocking(outStream.map(_.close())) // release
    }

  private def write[T](contents: T, stream: FileOutputStream, duration: FiniteDuration)(implicit
      encoder: Encoder[T]
  ): IO[Option[T]] = {
    val c = Cached(
      expires = Instant.now().plus((duration).toJava),
      value = contents
    )
    stream.write(c.asJson.noSpaces.getBytes())
    IO(Some(contents))
  }

  private def read[T](
      stream: FileInputStream
  )(implicit decoder: Decoder[T]): IO[Option[T]] = {
    IO.blocking {
      val string                      = scala.io.Source.fromInputStream(stream).mkString
      val c: Either[Error, Cached[T]] = decode[Cached[T]](string)
      c match {
        case Right(cached) if cached.expires.isAfter(Instant.now()) =>
          Some(cached.value)
        case _ => {
          None
        }
      }
    }
  }

  def put[T](v: T, destination: File, duration: FiniteDuration)(implicit
      encoder: Encoder[T]
  ): IO[Option[T]] = {
    outputStream(destination).use {
      case Left(_) => {
        logger.error(f"Could not open outputStream $destination")
        IO(None)
      }
      case Right(stream) => write(v, stream, duration)
    }
  }

  def get[T](source: File)(implicit decoder: Decoder[T]): IO[Option[T]] = {
    inputStream(source).use {
      case Left(_) => {
        logger.error(f"Could not open inputStream $source")
        IO(None)
      }
      case Right(stream) => read(stream)
    }
  }
}
