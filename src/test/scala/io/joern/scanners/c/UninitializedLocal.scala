package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal.iterableToTraversal

class UninitializedLocal extends Suite {

  override val code =
    """
      |void good1(bool which) {
      | char *s = "a";
      | puts(s);
      |}
      |void good2(bool which) {
      | char *s;
      | if (which)
      |   s = "a";
      | else
      |   s = "b";
      | puts(s);
      |}
      |
      |void bad1(bool which) {
      | char *s;
      | puts(s);
      |}
      |void bad2(bool which) {
      | char *s;
      | if (which)
      |   s = "a";
      | puts(s);
      |}
      |""".stripMargin

  "should flag functions `bad1` and `bad2` only" in {
    val x = UninitalizedLocal.uninitLocal()
    x(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Identifier]
      .map(_.method.name)
      .toSet shouldBe Set("bad1", "bad2")
  }

}
