package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.macros.QueryMacros._

object Metrics extends QueryBundle {

  @q
  def tooManyParameters(n: Int = 4): Query =
    Query.make(
      "too-many-params",
      Crew.fabs,
      s"Number of parameters larger than $n",
      s"This query identifies functions with more than $n formal parameters",
      1.0, { cpg =>
        cpg.method.internal.filter(_.parameter.size > n)
      },
      List(QueryTags.metrics)
    )

  @q
  def tooHighComplexity(n: Int = 4): Query =
    Query.make(
      "too-high-complexity",
      Crew.fabs,
      s"Cyclomatic complexity higher than $n",
      s"This query identifies functions with a cyclomatic complexity higher than $n",
      1.0, { cpg =>
        cpg.method.internal.filter(_.controlStructure.size > n)
      },
      List(QueryTags.metrics)
    )

  @q
  def tooLong(n: Int = 1000): Query =
    Query.make(
      "too-long",
      Crew.fabs,
      s"More than $n lines",
      s"This query identifies functions that are more than $n lines long",
      1.0, { cpg =>
        cpg.method.internal.filter(_.numberOfLines > n)
      },
      List(QueryTags.metrics)
    )

  @q
  def multipleReturns(): Query =
    Query.make(
      "multiple-returns",
      Crew.fabs,
      s"Multiple returns",
      "This query identifies functions with more than one return",
      1.0, { cpg =>
        cpg.method.internal.filter(_.ast.isReturn.l.size > 1)
      },
      List(QueryTags.metrics)
    )

  @q
  def tooManyLoops(n: Int = 4): Query =
    Query.make(
      "too-many-loops",
      Crew.fabs,
      s"More than $n loops",
      s"This query identifies functions with more than $n loops",
      1.0, { cpg =>
        cpg.method.internal
          .filter(
            _.ast.isControlStructure
              .parserTypeName("(For|Do|While).*")
              .size > n)
      },
      List(QueryTags.metrics)
    )

  @q
  def tooNested(n: Int = 3): Query =
    Query.make(
      "too-nested",
      Crew.fabs,
      s"Nesting level higher than $n",
      s"This query identifies functions with a nesting level higher than $n",
      1.0, { cpg =>
        cpg.method.internal.filter(_.depth(_.isControlStructure) > n)
      },
      List(QueryTags.metrics)
    )

}
