package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._

object SignedLeftShift extends QueryBundle {

  @q
  def signedLeftShift(): Query = Query(
    name = "signed-left-shift",
    author = Crew.malte,
    title = "Signed Shift May Cause Undefined Behavior",
    description =
      """
        |Signed integer overflow is undefined behavior. Shifts of signed values to the
        |left are very prone to overflow.
        |""".stripMargin,
    score = 2,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      cpg.call
        .nameExact("<operator>.shiftLeft")
        .where(_.argument(1).typ.fullNameExact("int", "long"))
        .not(_.and(_.argument(1).isLiteral, _.argument(2).isLiteral))
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

}
