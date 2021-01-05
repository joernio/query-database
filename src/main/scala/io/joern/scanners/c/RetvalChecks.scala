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
      cpg
        .call("(read|recv)")
        .whereNot(_.inAstMinusLeaf.isControlStructure)
        .whereNot(_.inAssignment)
    }
  )

}
