package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._

object RetvalChecks extends QueryBundle {

  def uncheckedRead(): Query = Query(
    name = "unchecked-read-recv",
    author = Crew.fabs,
    title = "Unchecked read/recv",
    description = "<description>",
    score = 3.0, { cpg =>
      val callsNotDirectlyChecked = cpg
        .call("(read|recv)")
        .whereNot(_.inAstMinusLeaf.isControlStructure)
        .l

      callsNotDirectlyChecked.filterNot { call =>
        val identifiersInCheck =
          call.method.controlStructure.condition.ast.isIdentifier.name.toSet
        val targets = call.inAssignment.target.code.toSet
        (targets & identifiersInCheck).nonEmpty
      }

    }
  )

}
