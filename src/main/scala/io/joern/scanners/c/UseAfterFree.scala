package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object UseAfterFree extends QueryBundle {

  @q
  def freeFieldNoReassign()(implicit context: EngineContext): Query = Query(
    name = "free-field-no-reassign",
    author = Crew.fabs,
    title = "A field of a parameter is free and not reassigned on all paths",
    description =
      """
        | The function is able to modify a field of a structure passed in by
        | the caller. It frees this field and does not guarantee that on
        | all paths to the exit, the field is reassigned. If any
        | caller now accesses the field, then it accesses memory that is no
        | longer allocated.
        |""".stripMargin,
    score = 5.0, { cpg =>
      val freeOfStructField = cpg
        .call("free")
        .where(
          _.argument(1).ast
            .isCallTo("<operator>.*[fF]ieldAccess.*")
            .filter(x =>
              x.method.parameter.name.toSet.contains(x.argument(1).code)))
        .l

      freeOfStructField.argument(1).filter { arg =>
        val exitNode = arg.method.methodReturn
        exitNode.reachableBy(arg).nonEmpty
      }

    }
  )

}
