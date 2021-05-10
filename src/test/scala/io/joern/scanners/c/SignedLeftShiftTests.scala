package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class SignedLeftShiftTests extends QueryTestSuite {

  override def queryBundle = SignedLeftShift

  "find signed left shift" in {
    queryBundle
      .signedLeftShift()(cpg)
      .flatMap(_.evidence)
      .map {
        case c: nodes.Call =>
          c.method.name
        case _ => fail()
      }
      .toSet shouldBe Set("bad1", "bad2", "bad3")
  }

}
