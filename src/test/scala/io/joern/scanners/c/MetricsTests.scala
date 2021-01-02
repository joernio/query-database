package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._

class MetricsTests extends Suite {

  override val code = """
    int too_many_params(int a, int b, int c, int d, int e) {
    }

    int high_cyclomatic_complexity(int x) {
      while(true) {
        for(int i = 0; i < 10; i++) {
        }
        if(foo()) {}
      }
      if (x > 10) {
        for(int i = 0; i < 10; i++) {

         }
      }
    }

    int func_with_many_lines(int x) {
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
      x++;
    }

    int func_with_multiple_returns (int x) {
      if (x > 10) {
        return 0;
      } else {
        return 1;
      }
    }

    int func_with_nesting_level_of_3(int foo, int bar) {
      if (foo > 10) {
        if (bar > foo) {
          for(int i = 0; i < bar ;i++) {

          }
        }
      }
    }

    int high_number_of_loops () {
      for(int i = 0; i < 10; i++){
      }
      int j = 0;
      do {
        j++
      } while(j < 10);
      while(foo()) {}
      while(bar()){}
    }

    """

  "find functions with too many parameters" in {
    Metrics.tooManyParameters(4)(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "too_many_params"
      case _ => fail
    }
  }

  "find functions with high cyclomatic complexity" in {
    Metrics.tooHighComplexity(4)(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "high_cyclomatic_complexity"
      case _ => fail
    }
  }

  "find functions that are long (in terms of line numbers)" in {
    Metrics.tooLong(13)(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "func_with_many_lines"
      case _ => fail
    }
  }

  "find functions with multiple returns" in {
    Metrics.multipleReturns()(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "func_with_multiple_returns"
      case _ => fail
    }
  }

  "find functions with high number of loops" in {
    Metrics.tooManyLoops(3)(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "high_number_of_loops"
      case _ => fail
    }
  }

  "find deeply nested functions" in {
    Metrics.tooNested(2)(cpg).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "func_with_nesting_level_of_3"
      case _ => fail
    }
  }

}
