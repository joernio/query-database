package io.joern.scanners.c.codequality

import io.joern.scanners.language._
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

/**
  * Joern requires each extension to provide a class derived from `LayerCreator`
  * and an associated companion object that provides the extension's name, description
  * and a method to retrieve its default options.
  * */
object CodeQualityScanner {
  val overlayName = "c-quality-scanner"
  val description = "Code quality scanner for C code"
  def defaultOpts = new CodeQualityScannerOptions()
}

class CodeQualityScannerOptions() extends LayerCreatorOptions {}

class CodeQualityScanner(options: CodeQualityScannerOptions)
    extends LayerCreator {
  override val overlayName: String = CodeQualityScanner.overlayName
  override val description: String = CodeQualityScanner.description

  /**
    * This method is called when the scanner is started
    * */
  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new CodeQualityPass(context.cpg), context, storeUndoInfo)
    outputFindings(context.cpg)
  }
}

class CodeQualityPass(cpg: Cpg) extends CpgPass(cpg) {
  import Metrics._

  /**
    * All we do here is call all queries and add a node to
    * the graph for each result.
    * */
  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    (tooManyParameters()(cpg) ++ tooManyLoops()(cpg) ++ tooNested()(cpg) ++
      tooLong()(cpg) ++ tooHighComplexity()(cpg) ++ multipleReturns()(cpg))
      .foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }

}
