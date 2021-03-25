package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.macros.QueryMacros._

object Metrics extends QueryBundle {

  @q
  def tooManyParameters(n: Int = 4): Query =
    Query.make(
      name = "too-many-params",
      author = Crew.fabs,
      title = s"Number of parameters larger than $n",
      description =
        s"This query identifies functions with more than $n formal parameters",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal.filter(_.parameter.size > n)
      }),
      tags = List(QueryTags.metrics)
    )

  @q
  def tooHighComplexity(n: Int = 4): Query =
    Query.make(
      name = "too-high-complexity",
      author = Crew.fabs,
      title = s"Cyclomatic complexity higher than $n",
      description =
        s"This query identifies functions with a cyclomatic complexity higher than $n",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal.filter(_.controlStructure.size > n)
      }),
      tags = List(QueryTags.metrics)
    )

  @q
  def tooLong(n: Int = 1000): Query =
    Query.make(
      name = "too-long",
      author = Crew.fabs,
      title = s"More than $n lines",
      description =
        s"This query identifies functions that are more than $n lines long",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal.filter(_.numberOfLines > n)
      }),
      tags = List(QueryTags.metrics)
    )

  @q
  def multipleReturns(): Query =
    Query.make(
      name = "multiple-returns",
      author = Crew.fabs,
      title = s"Multiple returns",
      description = "This query identifies functions with more than one return",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal.filter(_.ast.isReturn.l.size > 1)
      }),
      tags = List(QueryTags.metrics)
    )

  @q
  def tooManyLoops(n: Int = 4): Query =
    Query.make(
      name = "too-many-loops",
      author = Crew.fabs,
      title = s"More than $n loops",
      description = s"This query identifies functions with more than $n loops",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal
          .filter(
            _.ast.isControlStructure
              .parserTypeName("(For|Do|While).*")
              .size > n)
      }),
      tags = List(QueryTags.metrics)
    )

  @q
  def tooNested(n: Int = 3): Query =
    Query.make(
      name = "too-nested",
      author = Crew.fabs,
      title = s"Nesting level higher than $n",
      description =
        s"This query identifies functions with a nesting level higher than $n",
      score = 1.0,
      withStrRep({ cpg =>
        cpg.method.internal.filter(_.depth(_.isControlStructure) > n)
      }),
      tags = List(QueryTags.metrics)
    )

}
