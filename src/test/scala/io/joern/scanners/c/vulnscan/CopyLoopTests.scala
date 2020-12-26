package io.joern.scanners.c.vulnscan

import io.joern.scanners.c.Suite
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._

class CopyLoopTests extends Suite {

  override val code =
    """
      int index_into_dst_array (char *dst, char *src, int offset) {
        for(i = 0; i < strlen(src); i++) {
          dst[i + + j*8 + offset] = src[i];
        }
      }

      // We do not want to detect this one because the
      // index only specifies where to read from
      int index_into_src_array() {
        for(i = 0; i < strlen(src); i++) {
          dst[k] = src[i];
        }
      }

    """

  "find indexed buffer assignment targets in loops where index is incremented" in {
    CopyLoops.isCopyLoop(cpg: Cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "index_into_dst_array"
      case _ => fail
    }
  }

}
