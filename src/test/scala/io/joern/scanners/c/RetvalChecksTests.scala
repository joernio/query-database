package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class RetvalChecksTests extends QueryTestSuite {

  override def queryBundle = RetvalChecks

  "should find unchecked read and not flag others" in {
    val query = queryBundle.uncheckedReadRecvMalloc()
    val results = findMatchingCalls(query)

    results shouldBe Set("unchecked_read", "checks_something_else")
  }

}
