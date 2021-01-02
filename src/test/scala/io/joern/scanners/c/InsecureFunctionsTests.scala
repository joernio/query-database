package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

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

      int insecure_printf() {
        printf(argv[1], 4242);
      }

      int secure_printf() {
        printf("Num: %d", 4242);
      }

      int insecure_sprintf() {
        char buffer [BUFF_SIZE];
        sprintf(buffer, argv[2], 4242);
      }

      int secure_sprintf() {
        char buffer [BUFF_SIZE];
        snprintf(buffer, BUFF_SIZE, argv[2], 4242);
      }

    """

  "find insecure gets() function usage" in {
    InsecureFunctions.getsUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_gets"
      case _ => fail
    }
  }

  "find insecure printf() function usage" in {
    val results = InsecureFunctions.argvUsedInPrintf()(cpg).flatMap(_.evidence)
      .collect { case x : nodes.Call => x }.method.name
      .toSet
    results shouldBe Set("insecure_sprintf", "insecure_printf")
  }

}
