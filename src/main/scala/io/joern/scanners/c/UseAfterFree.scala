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
    title = "A field of a parameter is free'd and not reassigned on all paths",
    description =
      """
        | The function is able to modify a field of a structure passed in by
        | the caller. It frees this field and does not guarantee that on
        | all paths to the exit, the field is reassigned. If any
        | caller now accesses the field, then it accesses memory that is no
        | longer allocated. We also check that the function does not free
        | the entire structure, as in that case, it is unlikely that the
        | passed in structure will be used again.
        |""".stripMargin,
    score = 5.0,
    traversal = { cpg =>
      val freeOfStructField = cpg
        .call("free")
        .where(
          _.argument(1)
            .isCallTo("<operator>.*[fF]ieldAccess.*")
            .filter(x =>
              x.method.parameter.name.toSet.contains(x.argument(1).code))
        )
        .whereNot(_.argument(1).isCall.argument(1).filter { struct =>
          struct.method.ast.isCall
            .name("free")
            .argument(1)
            .codeExact(struct.code)
            .nonEmpty
        })
        .l

      freeOfStructField.argument(1).filter { arg =>
        arg.method.methodReturn.reachableBy(arg).nonEmpty
      }

    }
  )

}
