package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._

object IntegerTruncations extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  /**
    * Identify calls to `strlen` where return values are assigned
    * to variables of type `int`, potentially causing truncation
    * on 64 bit platforms.
    * */
  @q
  def strlenAssignmentTruncations(): Query =
    Query.make(
      name = "strlen-truncation",
      author = Crew.fabs,
      title = "Truncation in assignment involving `strlen` call",
      description =
        """
        |The return value of `strlen` is stored in a variable that is known
        |to be of type `int` as opposed to `size_t`. `int` is only 32 bit
        |wide on many 64 bit platforms, and thus, this may result in a
        |truncation.
        |""".stripMargin,
      score = 2,
      withStrRep({ cpg =>
        cpg.method
          .name("(?i)strlen")
          .callIn
          .inAssignment
          .target
          .evalType("(g?)int")
      }),
      tags = List(QueryTags.integers, QueryTags.default)
    )
}
