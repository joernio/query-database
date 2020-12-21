package io.joern.scanners.c.codequality

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.passes.{CpgPass, DiffGraph}
import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

object CodeQualityScanner {
  val overlayName = "c-vuln-scan"
  val description = "Vulnerability scanner for C code"
  def defaultOpts = new CodeQualityScannerOptions()
}

class CodeQualityScannerOptions() extends LayerCreatorOptions {}

class CScanner(options: CodeQualityScannerOptions) extends LayerCreator {
  override val overlayName: String = CodeQualityScanner.overlayName
  override val description: String = CodeQualityScanner.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new CodeQualityPass(context.cpg), context, storeUndoInfo)
  }
}

class CodeQualityPass(cpg: Cpg) extends CpgPass(cpg) {
  import Metrics._
  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    (tooManyParameters(cpg) ++ tooManyLoops(cpg) ++ tooNested(cpg) ++
      tooLong(cpg) ++ tooHighComplexity(cpg) ++ multipleReturns(cpg))
      .foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }
}
