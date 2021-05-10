package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class RetvalChecksTests extends QueryTestSuite {

  override def queryBundle = RetvalChecks

  "should find unchecked read and not flag others" in {
    val results =
      queryBundle.uncheckedReadRecvMalloc()(cpg).flatMap(_.evidence).collect {
        case call: nodes.Call => call.method.name
      }
    results.toSet shouldBe Set("unchecked_read", "checks_something_else")
  }

}
