package io.joern.scanners.c.vulnscan

import io.joern.scanners.c.{InsecureFunctions, Suite}
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._

class InsecureFunctionsTests extends Suite {

  override val code =
    """
      int insecure_gets() {
          char str[DST_BUFFER_SIZE];
          gets(str);
          printf("%s", str);
          return 0;
      }

      int secure_gets() {
          FILE *fp;
          fp = fopen("file.txt" , "r");
          char str[DST_BUFFER_SIZE];
          fgets(str, DST_BUFFER_SIZE, fp);
          printf("%s", str);
          return 0;
      }
    """

  "find insecure gets() function usage" in {
    InsecureFunctions.getsUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_gets"
      case _ => fail
    }
  }

}
