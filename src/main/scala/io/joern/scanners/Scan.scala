package io.joern.scanners

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

import scala.reflect.runtime.universe._

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

class JoernDefaultArgumentProvider(implicit context: EngineContext)
    extends DefaultArgumentProvider {

  override def defaultArgument(method: MethodSymbol,
                               im: InstanceMirror,
                               x: Symbol,
                               i: Int): Option[Any] = {
    if (x.typeSignature.toString.endsWith("EngineContext")) {
      Some(context)
    } else {
      super.defaultArgument(method, im, x, i)
    }
  }
}

class ScanPass(cpg: Cpg)(implicit engineContext: EngineContext)
    extends CpgPass(cpg) {

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    val queryDb = new QueryDatabase(new JoernDefaultArgumentProvider())
    queryDb.allQueries.foreach { query =>
      query(cpg).foreach(diffGraph.addNode)
    }
    Iterator(diffGraph.build)
  }

}
