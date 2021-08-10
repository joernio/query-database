package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class NullTerminationTests extends QueryTestSuite {

  override def queryBundle = NullTermination

  "should find the bad code and not report the good" in {

    val query = queryBundle.strncpyNoNullTerm()
    val results = query(cpg)
      .flatMap(_.evidence)
      .collect { case expr: nodes.Expression => expr }
      .method
      .name
      .toSetImmutable

    results shouldBe Set("bad")
//    val query = queryBundle.strncpyNoNullTerm()
//    query(cpg).flatMap(_.evidence) match {
//      case List(x: nodes.Expression) =>
//        x.method.name shouldBe "bad"
//      case _ =>
//        fail()
//    }
  }

}
