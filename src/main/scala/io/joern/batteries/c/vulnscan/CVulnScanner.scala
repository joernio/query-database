package io.joern.batteries.c.vulnscan

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

object CScanner {
  val overlayName = "c-vuln-scan"
  val description = "Vulnerability scanner for C code"
  def defaultOpts = new CScannerOptions()
}

class CScannerOptions() extends LayerCreatorOptions {}

class CScanner(options: CScannerOptions) extends LayerCreator {
  override val overlayName: String = CScanner.overlayName
  override val description: String = CScanner.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new CScannerPass(context.cpg), context, storeUndoInfo)
  }
}

class CScannerPass(cpg: Cpg) extends CpgPass(cpg) {
  import IntegerTruncations._
  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    strlenAssignmentTruncations(cpg).foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }
}
