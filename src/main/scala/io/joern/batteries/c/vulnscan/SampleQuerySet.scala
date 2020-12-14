package io.joern.batteries.c.vulnscan

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.passes.{CpgPass, DiffGraph}

object SampleQuerySet {

  def myQuery1(cpg : Cpg) : List[nodes.NewFinding] = {
    ???
  }

  def myQuery2(cpg : Cpg) : List[nodes.NewFinding] = {
    ???
  }
  // ...
}

class SampleQuertSet(cpg: Cpg) extends CpgPass(cpg) {

  import SampleQuerySet._

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    myQuery1(cpg).foreach(diffGraph.addNode)
    myQuery2(cpg).foreach(diffGraph.addNode)
    // ...
    Iterator(diffGraph.build)
  }

}


