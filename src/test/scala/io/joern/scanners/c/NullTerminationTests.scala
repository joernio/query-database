package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class NullTerminationTests extends Suite {

  override val code =
    """
      |  // Null-termination is ensured if we can only copy
      |  // less than `asize + 1` into the buffer
      |int good() {
      |  char *ptr = malloc(asize + 1);
      |  strncpy(ptr, src, asize);
      |}
      |
      |
      | // Null-termination is also ensured if it is performed
      | // explicitly
      |int alsogood() {
      |  char *ptr = malloc(asize);
      |  strncpy(ptr, src, asize);
      |  ptr[asize -1] = '\0';
      |}
      |
      |// If src points to a string that is at least `asize` long,
      |// then `ptr` will not be null-terminated after the `strncpy`
      |// call.
      |int bad() {
      |  char *ptr = malloc(asize);
      |  strncpy(ptr, src, asize);
      |}
      |
      |""".stripMargin

  "should find the bad code and not report the good" in {
    val x = NullTermination.strncpyNoNullTerm()
    x(cpg).flatMap(_.evidence) match {
      case List(x: nodes.Expression) =>
        x.method.name shouldBe "bad"
      case _ =>
        fail()
    }
  }

}
