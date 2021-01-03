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

      int insecure_scanf() {
        char name[12];
        scanf("%s", name);
        printf("Hello %s!\n", name);
        return 0
      }

      int insecure_strncat_strncpy() {
        char buf[BUF_SIZE];
        strncpy(buf, default_value, BUF_SIZE); // remediation is (BUFF_SIZE - 1) 
        strncat(buf, another_buffer, BUF_SIZE - strlen(buf)); // remediation is (BUFF_SIZE - strlen(buf) - 1)
        return 0
      }

      int insecure_strtok() {
        char *token;
        char *path = getenv("PATH");
        token = strtok(path, ":");
        puts(token);
        printf("PATH: %s\n", path); // original path string now has '/usr/bin\0' now and is insecure to use
        return 0;
      }

      int insecure_getwd() {
        char dir[12];
        getwd(buf);
        printf("Working directory:%s\n",buf);
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

  "find insecure printf() function usage" in {
    val results = InsecureFunctions
      .argvUsedInPrintf()(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet
    results shouldBe Set("insecure_sprintf", "insecure_printf")
  }

  "find insecure scanf() function usage" in {
    InsecureFunctions.scanfUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_scanf"
      case _ => fail
    }
  }

  "find insecure strncat() function usage" in {
    InsecureFunctions.strcatUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_strncat_strncpy"
      case _ => fail
    }
  }

  "find insecure strncpy() function usage" in {
    InsecureFunctions.strcpyUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_strncat_strncpy"
      case _ => fail
    }
  }

  "find insecure strtok() function usage" in {
    InsecureFunctions.strtokUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_strtok"
      case _ => fail
    }
  }

  "find insecure getwd() function usage" in {
    InsecureFunctions.getwdUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_getwd"
      case _ => fail
    }
  }

}
