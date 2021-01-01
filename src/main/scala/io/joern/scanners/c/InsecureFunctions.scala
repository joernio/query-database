package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._

object InsecureFunctions extends QueryBundle {

  @q
  def getsUsed(): Query = Query(
    name = "call-to-gets",
    title = "Insecure function gets() used",
    description =
      """
        | Avoid gets() function as it can lead to reads beyond buffer boundary and cause
        | buffer overflows. Some secure alternatives are fgets() and gets_s().
        |""".stripMargin,
    score = 4, { cpg =>
      cpg.call("gets")
    }
  )

}
