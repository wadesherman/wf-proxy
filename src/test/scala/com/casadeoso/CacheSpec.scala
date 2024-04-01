package com.casadeoso

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.casadeoso.cache.Cache
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.io.File
import scala.concurrent.duration.DurationInt

class CacheSpec extends AnyFlatSpec with OptionValues {

  implicit val runtime: IORuntime = IORuntime.global

  val sample = TestData(
    aString = "SomeString",
    anInt = 1,
    aBool = true,
    anOption = Some("OptionalString")
  )

  it should "read an active cached file and return a some" in {
    val activePath                   = "./src/test/resources/active.json"
    val cacheFile                    = new File(activePath)
    val active: IO[Option[TestData]] = Cache.get[TestData](cacheFile)
    active.unsafeRunSync() shouldBe Some(sample)

  }

  it should "read an expired cached file and return a none" in {
    val expiredPath                  = "./src/test/resources/expired.json"
    val cacheFile                    = new File(expiredPath)
    val active: IO[Option[TestData]] = Cache.get[TestData](cacheFile)
    active.unsafeRunSync() shouldBe Symbol("empty")

  }

  it should "return a none if the file is in an invalid format" in {

    val invalidPath                  = "./src/test/resources/invalid.json"
    val cacheFile                    = new File(invalidPath)
    val active: IO[Option[TestData]] = Cache.get[TestData](cacheFile)
    active.unsafeRunSync() shouldBe Symbol("empty")

  }

  it should "return a none if the file doesnt exist" in {

    val activePath                   = "./src/test/resources/doesntexist.json"
    val cacheFile                    = new File(activePath)
    val active: IO[Option[TestData]] = Cache.get[TestData](cacheFile)
    active.unsafeRunSync() shouldBe Symbol("empty")
  }

  it should "write a file with cached contents" in {
    val cachePath            = "/tmp/wp-test.json"
    val cacheFile            = new File(cachePath)
    val active: IO[TestData] = Cache.put[TestData](sample, cacheFile, 1.hour).map(_.get)
    val testData             = active.unsafeRunSync()
    testData shouldBe sample
  }

}
