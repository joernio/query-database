package io.joern.scanners

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

object Scan {
  val overlayName = "scan"
  val description = "Joern/Ocular Code Scanner"
  def defaultOpts = new ScanOptions()
}

class ScanOptions() extends LayerCreatorOptions {}

class Scan(options: ScanOptions)(implicit engineContext: EngineContext)
    extends LayerCreator {
  override val overlayName: String = Scan.overlayName
  override val description: String = Scan.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new ScanPass(context.cpg), context, storeUndoInfo)
    outputFindings(context.cpg)
  }
}

class ScanPass(cpg: Cpg)(implicit engineContext: EngineContext)
    extends CpgPass(cpg) {

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    val queryDb = new QueryDatabase()
    Iterator(diffGraph.build)
  }

}
