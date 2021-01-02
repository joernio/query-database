package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._

object InsecureFunctions extends QueryBundle {

  @q
  def getsUsed(): Query = Query(
    name = "call-to-gets",
    author = Crew.suchakra,
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

  @q
  def argvUsedInPrintf(): Query = Query(
    name = "format-controlled-printf",
    author = Crew.suchakra,
    title = "Function printf(), sprintf() or vsprintf() used insecurely",
    description =
      """
        | Avoid user controlled format strings like "argv" in printf, sprintf and vsprintf 
        | functions as they can cause buffer overflows. Some secure alternatives are 
        | snprintf() and vsnprintf().
        |""".stripMargin,
    score = 4, { cpg =>
      cpg
        .call("printf")
        .whereNot(_.argument.order(1).isLiteral) ++
      cpg
        .call("(sprintf|vsprintf)")
        .whereNot(_.argument.order(2).isLiteral)
    }
  )

}
