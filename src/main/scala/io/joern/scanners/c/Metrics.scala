package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._

object Metrics extends QueryBundle {

  @q
  def tooManyParameters(n: Int = 4): Query = Query(
    name = "too-many-params",
    title = s"Number of parameters larger than $n",
    description =
      s"This query identifies functions with more than $n formal parameters",
    score = 2.0, { cpg =>
      cpg.method.internal.filter(_.parameter.size > n)
    }
  )

  @q
  def tooHighComplexity(n: Int = 4): Query = Query(
    name = "too-high-complexity",
    title = s"Cyclomatic complexity higher than $n",
    description =
      s"This query identifies functions with a cyclomatic complexity higher than $n",
    score = 2.0, { cpg =>
      cpg.method.internal.filter(_.controlStructure.size > n)
    }
  )

  @q
  def tooLong(n: Int = 1000): Query = Query(
    name = "too-long",
    title = s"More than $n lines",
    description =
      s"This query identifies functions that are more than $n lines long",
    score = 2.0, { cpg =>
      cpg.method.internal.filter(_.numberOfLines > n)
    }
  )

  @q
  def multipleReturns(): Query = Query(
    name = "multiple-returns",
    title = s"Multiple returns",
    description = "This query identifies functions with more than one return",
    score = 2.0, { cpg =>
      cpg.method.internal.filter(_.ast.isReturn.l.size > 1)
    }
  )

  @q
  def tooManyLoops(n: Int = 4): Query = Query(
    name = "too-many-loops",
    title = s"More than $n loops",
    description = s"This query identifies functions with more than $n loops",
    score = 2, { cpg =>
      cpg.method.internal
        .filter(
          _.ast.isControlStructure.parserTypeName("(For|Do|While).*").size > n)
    }
  )

  @q
  def tooNested(n: Int = 3): Query = Query(
    name = "too-nested",
    title = s"Nesting level higher than $n",
    description =
      s"This query identifies functions with a nesting level higher than $n",
    score = 2, { cpg =>
      cpg.method.internal.filter(_.depth(_.isControlStructure) > n)
    }
  )

}
