package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal.iterableToTraversal

class SocketApiTests extends QueryTestSuite {

  override def queryBundle = SocketApi

  "should flag function `return_not_checked` only" in {
    val results =
      queryBundle.uncheckedSend()(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet
    results shouldBe Set("return_not_checked")
  }

}
