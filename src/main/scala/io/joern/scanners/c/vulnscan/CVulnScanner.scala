package io.joern.scanners.c.vulnscan

import io.joern.scanners.language._
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
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

class CScanner(options: CScannerOptions)(implicit engineContext: EngineContext)
    extends LayerCreator {
  override val overlayName: String = CScanner.overlayName
  override val description: String = CScanner.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new CScannerPass(context.cpg), context, storeUndoInfo)
    outputFindings(context.cpg)
  }
}

class CScannerPass(cpg: Cpg)(implicit engineContext: EngineContext)
    extends CpgPass(cpg) {
  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    IntegerTruncations
      .strlenAssignmentTruncations(cpg)
      .foreach(diffGraph.addNode)
    HeapBasedOverflow.mallocMemcpyIntOverflow(cpg).foreach(diffGraph.addNode)
    CopyLoops.isCopyLoop(cpg).foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }
}
