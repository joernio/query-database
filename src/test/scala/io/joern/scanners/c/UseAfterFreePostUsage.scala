package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal.iterableToTraversal

class UseAfterFreePostUsage extends Suite {

  override val code =
    """
      |void *bad() {
      |  void *x = NULL;
      |  if (cond)
      |    free(x);
      |  return x;
      |}
      |
      |void *false_negative() {
      |  void *x = NULL;
      |  if (cond) {
      |    free(x);
      |    if (cond2)
      |      return x; // not post-dominated by free call
      |    x = NULL;
      |  }
      |  return x;
      |}
      |
      |void *false_positive() {
      |  void *x = NULL;
      |  free(x);
      |  if (cond)
      |    x = NULL;
      |  else
      |    x = NULL;
      |  return x;
      |}
      |
      |void *good() {
      |  void *x = NULL;
      |  if (cond)
      |    free(x);
      |  x = NULL;
      |  return x;
      |}
      |""".stripMargin

  "should flag functions `bad` and `false_positive` only" in {
    val x = UseAfterFree.freePostDominatesUsage()
    x(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Identifier]
      .method
      .name
      .toSet shouldBe Set("bad", "false_positive")
  }

}
