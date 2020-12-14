package io.joern.batteries.c.vulnscan

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.language._
import io.joern.batteries.lib._

object IntegerTruncations {

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

class IntegerTruncations(cpg: Cpg) extends CpgPass(cpg) {

  import IntegerTruncations._

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    strlenAssignmentTruncations(cpg).foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }

}
