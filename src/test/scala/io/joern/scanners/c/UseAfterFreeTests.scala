package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._

class UseAfterFreeTests extends Suite {

  override val code =
    """
      |void good(a_struct_type *a_struct) {
      |
      |  free(a_struct->ptr);
      |  if (something) {
      |    a_struct->ptr = NULL;
      |    return;
      |  }
      |  a_struct->ptr = foo;
      |}
      |
      |void bad(a_struct_type *a_struct) {
      | free(a_struct->ptr);
      | if (something) {
      |   return;
      | }
      | a_struct->ptr = foo;
      |}
      |
      |""".stripMargin

  "should flag `bad` function only" in {
    val x = UseAfterFree.freeFieldNoReassign()
    x(cpg)
      .flatMap(_.evidence)
      .collect { case call: nodes.Call => call.method.name }
      .toSet shouldBe Set("bad")
  }

}
