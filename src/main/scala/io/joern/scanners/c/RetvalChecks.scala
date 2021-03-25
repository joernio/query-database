package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._

object RetvalChecks extends QueryBundle {

  @q
  def uncheckedReadRecvMalloc(): Query =
    Query.make(
      "unchecked-read-recv-malloc",
      Crew.fabs,
      "Unchecked read/recv/malloc",
      """
      |The return value of a read/recv/malloc call is not checked directly and
      |the variable it has been assigned to (if any) does not
      |occur in any check within the caller.
      |""".stripMargin,
      3.0, { cpg =>
        implicit val noResolve: NoResolve.type = NoResolve
        val callsNotDirectlyChecked = cpg
          .method("(read|recv|malloc)")
          .callIn
          .filterNot { y =>
            val code = y.code
            y.inAstMinusLeaf.isControlStructure.condition.code.exists { x =>
              x.contains(code)
            }
          }
          .l

        callsNotDirectlyChecked.filterNot { call =>
          val inConditions = call.method.controlStructure.condition.ast.l;
          val checkedVars = inConditions.isIdentifier.name.toSet ++ inConditions.isCall.code.toSet;
          val targets = call.inAssignment.target.code.toSet
          (targets & checkedVars).nonEmpty
        }
      },
      List()
    )

}
