package io.joern.scanners.c.codequality

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._

object Metrics {

  def tooManyParameters(n: Int = 4): Query = Query(
    title = s"Number of parameters larger than $n",
    description =
      s"This query identifies functions with more than $n formal parameters",
    score = 2.0, { cpg =>
      cpg.method.filter(_.parameter.size > n)
    }
  )

  def tooHighComplexity(n: Int = 4): Query = Query(
    title = s"Cyclomatic complexity higher than $n",
    description =
      s"This query identifies functions with a cyclomatic complexity higher than $n",
    score = 2.0, { cpg =>
      cpg.method.filter(_.controlStructure.size > n)
    }
  )

  def tooLong(n: Int = 1000): Query = Query(
    title = s"More than $n lines",
    description =
      s"This query identifies functions that are more than $n lines long",
    score = 2.0, { cpg =>
      cpg.method.filter(_.numberOfLines > n)
    }
  )

  def multipleReturns(): Query = Query(
    title = s"Multiple returns",
    description = "This query identifies functions with more than one return",
    score = 2.0, { cpg =>
      cpg.method.filter(_.ast.isReturn.l.size > 1)
    }
  )

  def tooManyLoops(n: Int = 4): Query = Query(
    title = s"More than $n loops",
    description = s"This query identifies functions with more than $n loops",
    score = 2, { cpg =>
      cpg.method
        .filter(
          _.ast.isControlStructure.parserTypeName("(For|Do|While).*").size > n)
    }
  )

  def tooNested(n: Int = 3): Query = Query(
    title = s"Nesting level higher than $n",
    description =
      s"This query identifies functions with a nesting level higher than $n",
    score = 2, { cpg =>
      cpg.method.filter(_.depth(_.isControlStructure) > n)
    }
  )

}
