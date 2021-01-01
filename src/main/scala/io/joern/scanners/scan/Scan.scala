package io.joern.scanners.scan

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.console.{DefaultArgumentProvider, Query, QueryDatabase}
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.passes.{CpgPass, DiffGraph, KeyPoolCreator, ParallelCpgPass}
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
    val queryDb = new QueryDatabase(new JoernDefaultArgumentProvider())
    val allQueries: List[Query] = queryDb.allQueries
    runPass(new ScanPass(context.cpg, allQueries), context, storeUndoInfo)
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

class ScanPass(cpg: Cpg, queries: List[Query])(
    implicit engineContext: EngineContext)
    extends ParallelCpgPass[Query](
      cpg,
      keyPools =
        Some(KeyPoolCreator.obtain(queries.size, 42949672950L).iterator)) {

  override def partIterator: Iterator[Query] = queries.iterator

  override def runOnPart(query: Query): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    query(cpg).foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }

}
