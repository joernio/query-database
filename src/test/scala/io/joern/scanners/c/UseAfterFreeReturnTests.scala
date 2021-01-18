package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal.iterableToTraversal

class UseAfterFreeReturnTests extends Suite {

  override val code =
    """
      |void good1(a_struct_type *a_struct) {
      |  void *x = NULL, *y = NULL;
      |  a_struct->foo = x;
      |  free(y);
      |}
      |
      |void good2(a_struct_type *a_struct) {
      |  void *x = NULL;
      |  free(a_struct->foo);
      |  a_struct->foo = x;
      |}
      |
      |void bad(a_struct_type *a_struct) {
      |  void *x = NULL;
      |  a_struct->foo = x;
      |  free(x);
      |}
      |
      |void bad_not_covered(a_struct_type *a_struct) {
      |  void *x = NULL;
      |  a_struct->foo = x;
      |  free(a_struct->foo);
      |}
      |""".stripMargin

  "should flag `bad` function only" in {
    val x = UseAfterFree.freeReturnedValue()
    x(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Identifier]
      .method
      .name
      .toSet shouldBe Set("bad")
  }

}
