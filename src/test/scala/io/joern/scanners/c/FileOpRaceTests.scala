package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class FileOpRaceTests extends QueryTestSuite {

  override def queryBundle = FileOpRace

  "should flag function `insecure_race` only" in {
    val queries = queryBundle.fileOperationRace()
    val results = queries(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet
    results shouldBe Set("insecure_race")
  }

}
