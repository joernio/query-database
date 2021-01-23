package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class SignedLeftShiftTests extends Suite {

  override val code =
    """
      void bad1(int val) {
        val <<= 24;
      }
      void bad2(int val) {
        255 << val;
      }
      void bad3(int val) {
        val << val;
      }

      void good(unsigned int val) {
        255 << 24; // we ignore signed shift with two literals
        val <<= 24;
        val << val;
      }
    """

  "find signed left shift" in {
    SignedLeftShift
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
