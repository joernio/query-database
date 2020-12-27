package io.joern.scanners.c.vulnscan

import io.joern.scanners.language._
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._

object CopyLoops {

  /**
    * For (buf, indices) pairs, determine those inside control structures (for, while, if ...)
    * where any of the calls made outside of the body (block) are Inc operations. Determine
    * the first argument of that Inc operation and check if they are used as indices for
    * the write operation into the buffer.
    * */
  def isCopyLoop(cpg: Cpg): List[nodes.NewFinding] = {
    cpg.assignment.target.isArrayAccess
      .map { access =>
        (access.array, access.subscripts.code.toSet)
      }
      .filter {
        case (buf, subscripts) =>
          val incIdentifiers = buf.inAst.isControlStructure.astChildren
            .filterNot(_.isBlock)
            .assignments
            .target
            .code
            .toSet
          (incIdentifiers & subscripts).nonEmpty
      }
      .map(_._1)
      .map(
        finding(_, title = "Copy loop detected", description = "-", score = 2)
      )
      .l

  }

}
