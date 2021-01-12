package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._

object Metrics extends QueryBundle {

  @q
  def tooManyParameters(n: Int = 4): Query = Query(
    name = "too-many-params",
    author = Crew.fabs,
    title = s"Number of parameters larger than $n",
    description =
      s"This query identifies functions with more than $n formal parameters",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal.filter(_.parameter.size > n)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def tooHighComplexity(n: Int = 4): Query = Query(
    name = "too-high-complexity",
    author = Crew.fabs,
    title = s"Cyclomatic complexity higher than $n",
    description =
      s"This query identifies functions with a cyclomatic complexity higher than $n",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal.filter(_.controlStructure.size > n)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def tooLong(n: Int = 1000): Query = Query(
    name = "too-long",
    author = Crew.fabs,
    title = s"More than $n lines",
    description =
      s"This query identifies functions that are more than $n lines long",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal.filter(_.numberOfLines > n)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def multipleReturns(): Query = Query(
    name = "multiple-returns",
    author = Crew.fabs,
    title = s"Multiple returns",
    description = "This query identifies functions with more than one return",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal.filter(_.ast.isReturn.l.size > 1)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def tooManyLoops(n: Int = 4): Query = Query(
    name = "too-many-loops",
    author = Crew.fabs,
    title = s"More than $n loops",
    description = s"This query identifies functions with more than $n loops",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal
        .filter(
          _.ast.isControlStructure.parserTypeName("(For|Do|While).*").size > n)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def tooNested(n: Int = 3): Query = Query(
    name = "too-nested",
    author = Crew.fabs,
    title = s"Nesting level higher than $n",
    description =
      s"This query identifies functions with a nesting level higher than $n",
    score = 1.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.method.internal.filter(_.depth(_.isControlStructure) > n)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

}
