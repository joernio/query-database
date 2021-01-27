package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.codepropertygraph.generated.Operators
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._

object SignedLeftShift extends QueryBundle {

  @q
  def signedLeftShift(): Query =
    queryInit(
      "signed-left-shift",
      Crew.malte,
      "Signed Shift May Cause Undefined Behavior",
      """
        |Signed integer overflow is undefined behavior. Shifts of signed values to the
        |left are very prone to overflow.
        |""".stripMargin,
      2, { cpg =>
        cpg.call
          .nameExact(Operators.shiftLeft, Operators.assignmentShiftLeft)
          .where(_.argument(1).typ.fullNameExact("int", "long"))
          .filterNot(_.argument.isLiteral.size == 2) // assume such constant values produces a correct result
      },
    ).asInstanceOf[Query]

}
