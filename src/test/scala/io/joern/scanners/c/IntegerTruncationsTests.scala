package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class IntegerTruncationsTests extends Suite {

  override val code: String =
    """
      int vulnerable(char *str) {
        int len;
        len = strlen(str);
      }

      int non_vulnerable(char *str) {
        size_t len;
        len = strlen(str);
      }
    """

  "find truncation in assignment of `strlen` to `int`" in {
    IntegerTruncations.strlenAssignmentTruncations()(cpg) match {
      case List(result) =>
        result.evidence match {
          case List(x: nodes.Identifier) => x.method.name shouldBe "vulnerable"
          case _                         => fail
        }
      case _ => fail
    }
  }

}
