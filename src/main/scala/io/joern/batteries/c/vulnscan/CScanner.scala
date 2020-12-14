package io.joern.batteries.c.vulnscan

import io.shiftleft.semanticcpg.layers.{
  LayerCreator,
  LayerCreatorContext,
  LayerCreatorOptions
}

object CScanner {
  val overlayName = "c-vuln-scan"
  val description = "Vulnerability scanner for C code"
}

class CScannerOptions() extends LayerCreatorOptions {}

class CScanner(options: CScannerOptions) extends LayerCreator {
  override val overlayName: String = CScanner.overlayName
  override val description: String = CScanner.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new IntegerTruncations(context.cpg), context, storeUndoInfo)
  }
}
