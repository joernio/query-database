package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.Query
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._

class DangerousFunctionsTests extends QueryTestSuite {
  override def queryBundle = DangerousFunctions

  "find insecure gets() function usage" in {
    queryBundle.getsUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>{
        expr.method.name shouldBe "insecure_gets"
      }
      case _ => fail()
    }
  }

  "find insecure printf() function usage" in {
    val results =
      queryBundle.argvUsedInPrintf()(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet
    results shouldBe Set("insecure_sprintf", "insecure_printf")
  }

  "find insecure scanf() function usage" in {
    queryBundle.scanfUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_scanf"
      case _ => fail()
    }
  }

  "find insecure strncat() function usage" in {
    val results =
      queryBundle.strcatUsed()(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet

    results shouldBe Set("insecure_strcat", "insecure_strncat")
  }

  "find insecure strncpy() function usage" in {
    val results =
      queryBundle.strcpyUsed()(cpg)
      .flatMap(_.evidence)
      .collect { case x: nodes.Call => x }
      .method
      .name
      .toSet

    results shouldBe Set("insecure_strcpy", "insecure_strncpy")
  }

  "find insecure strtok() function usage" in {
    queryBundle.strtokUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_strtok"
      case _ => fail()
    }
  }

  "find insecure getwd() function usage" in {
    queryBundle.getwdUsed()(cpg).map(_.evidence) match {
      case List(List(expr: nodes.Expression)) =>
        expr.method.name shouldBe "insecure_getwd"
      case _ => fail()
    }
  }
}
