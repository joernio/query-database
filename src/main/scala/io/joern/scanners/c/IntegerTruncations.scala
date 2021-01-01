package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._

object IntegerTruncations extends QueryBundle {

  /**
    * Identify calls to `strlen` where return values are assigned
    * to variables of type `int`, potentially causing truncation
    * on 64 bit platforms.
    * */
  @q
  def strlenAssignmentTruncations(): Query = Query(
    name = "strlen-truncation",
    author = Crew.fabs,
    title = "Truncation in assigment involving strlen call",
    description = "-",
    score = 2, { cpg =>
      cpg
        .call("strlen")
        .inAssignment
        .target
        .evalType("(g?)int")
    }
  )
}
