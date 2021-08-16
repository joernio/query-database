package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class CopyLoopTests extends QueryTestSuite {
  override def queryBundle = CopyLoops

  "find indexed buffer assignment targets in loops where index is incremented" in {
    queryBundle.isCopyLoop()(cpg).map(_.evidence) match {
      case List(IndexedSeq(expr: nodes.Expression)) =>
        expr.method.name shouldBe "index_into_dst_array"
      case _ => fail()
    }
  }

}
