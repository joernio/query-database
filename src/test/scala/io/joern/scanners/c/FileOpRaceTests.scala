package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.dataflowengineoss.semanticsloader.{FlowSemantic, Parser, Semantics}
import io.shiftleft.semanticcpg.language._

class FileOpRaceTests extends QueryTestSuite {

  override def queryBundle = FileOpRace

  override val code: String =
     """
      |void insecure_race(char *path) {
      |    chmod(path, 0);
      |    rename(path, "/some/new/path");
      |}
      |void secure_handle(char *path) {
      |    FILE *file = fopen(path, "r");
      |    fchown(fileno(file), 0, 0);
      |}
      |""".stripMargin

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
