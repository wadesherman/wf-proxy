package com.casadeoso

import com.casadeoso.transform.Transformer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TransformerSpec extends AnyFlatSpec with Matchers {
  it should "strip cc- off the current conditions icon name" in {
    Transformer.ccIconToIcon("cc-sunny") shouldBe "sunny"
    Transformer.ccIconToIcon("sunny") shouldBe "sunny"
  }

}
