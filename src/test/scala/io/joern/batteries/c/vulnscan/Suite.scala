package io.joern.batteries.c.vulnscan

import io.shiftleft.dataflowengineoss.language.DataFlowCodeToCpgSuite

class Suite extends DataFlowCodeToCpgSuite {

  override def beforeAll(): Unit = {
    semanticsFilename = "src/test/resources/default.semantics"
    super.beforeAll()
  }

}
