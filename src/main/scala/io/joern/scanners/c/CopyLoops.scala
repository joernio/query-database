package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._

object CopyLoops extends QueryBundle {

  @q
  def isCopyLoop(): Query =
    Query.make(
      "copy-loop",
      Crew.fabs,
      "Copy loop detected",
      """
        |For (buf, indices) pairs, determine those inside control structures (for, while, if ...)
        |where any of the calls made outside of the body (block) are Inc operations. Determine
        |the first argument of that Inc operation and check if they are used as indices for
        |the write operation into the buffer.
        |""".stripMargin,
      2, { cpg =>
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
      },
      List()
    )

}
