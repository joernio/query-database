package io.joern.scanners

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

object JoernScan {
  val overlayName = "joern-scan"
  val description = "Joern Code Scanner"
  def defaultOpts = new JoernScanOptions()
}

class JoernScanOptions() extends LayerCreatorOptions {}

class JoernScan(options: JoernScanOptions)(
    implicit engineContext: EngineContext)
    extends LayerCreator {
  override val overlayName: String = JoernScan.overlayName
  override val description: String = JoernScan.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new JoernScanPass(context.cpg), context, storeUndoInfo)
    outputFindings(context.cpg)
  }
}

class JoernScanPass(cpg: Cpg)(implicit engineContext: EngineContext)
    extends CpgPass(cpg) {

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    val queryDb = new QueryDatabase()
    Iterator(diffGraph.build)
  }

}
