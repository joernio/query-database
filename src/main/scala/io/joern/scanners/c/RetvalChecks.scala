package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._

object RetvalChecks extends QueryBundle {

  @q
  def uncheckedRead(): Query = Query(
    name = "unchecked-read-recv",
    author = Crew.fabs,
    title = "Unchecked read/recv",
    description =
      """
      |The return value of a read/recv call is not checked directly and the
      |variable its return value has been assigned to (if any) does not
      |occur in any check within the caller.
      |""".stripMargin,
    score = 3.0,
    traversal = { cpg =>
      val callsNotDirectlyChecked = cpg
        .call("(read|recv)")
        .whereNot(_.inAstMinusLeaf.isControlStructure)
        .l

      callsNotDirectlyChecked.filterNot { call =>
        val inConditions = call.method.controlStructure.condition.ast.l;
        val checkedVars = inConditions.isIdentifier.name.toSet ++ inConditions.isCall.code.toSet;
        val targets = call.inAssignment.target.code.toSet
        (targets & checkedVars).nonEmpty
      }
    }
  )

}
