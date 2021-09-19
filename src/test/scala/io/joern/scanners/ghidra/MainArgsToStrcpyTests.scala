package io.joern.scanners.ghidra

import io.joern.suites.GhidraQueryTestSuite

class MainArgsToStrcpyTests extends GhidraQueryTestSuite {
  override def queryBundle = MainArgsToStrcpy

  "find main function with data flow between argument and strcpy" in {
    buildCpgForBin("buf1.exe")
    val query = queryBundle.mainArgsToStrcpy()
    val results = findMatchingMethodParam(query)
    results shouldBe Set("main")
  }
}
