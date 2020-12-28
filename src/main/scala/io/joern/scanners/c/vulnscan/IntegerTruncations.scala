package io.joern.scanners.c.vulnscan

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.joern.scanners.language._

object IntegerTruncations {

  /**
    * Identify calls to `strlen` where return values are assigned
    * to variables of type `int`, potentially causing truncation
    * on 64 bit platforms.
    * */
  def strlenAssignmentTruncations(cpg: Cpg): List[nodes.NewFinding] = {
    cpg
      .call("strlen")
      .inAssignment
      .target
      .evalType("(g?)int")
      .map(
        finding(_,
                title = "Truncation in assigment involving strlen call",
                description = "-",
                score = 2)
      )
      .l
  }

}
