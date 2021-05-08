package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class NullTerminationTests extends QueryTestSuite {

  override def queryBundle = NullTermination

  "should find the bad code and not report the good" in {
    val x = queryBundle.strncpyNoNullTerm()
    x(cpg).flatMap(_.evidence) match {
      case List(x: nodes.Expression) =>
        x.method.name shouldBe "bad"
      case _ =>
        fail()
    }
  }

}
