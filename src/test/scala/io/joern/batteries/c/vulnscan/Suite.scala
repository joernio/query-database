package io.joern.batteries.c.vulnscan

import io.shiftleft.fuzzyc2cpg.testfixtures.DataFlowCodeToCpgSuite

class Suite extends DataFlowCodeToCpgSuite {

  override def beforeAll(): Unit = {
    semanticsFilename = "src/test/resources/default.semantics"
    super.beforeAll()
  }

}
