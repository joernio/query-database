package io.joern.scanners.c

import io.joern.scanners.language.Query
import io.joern.scanners.{QueryBundle, query}
import io.shiftleft.semanticcpg.language._

object InsecureFunctions extends QueryBundle {

  @query
  def getsUsed(): Query = Query(
    title = "Insecure function gets() used",
    description =
      "Avoid gets() function as it can lead to reads beyond buffer boundary and cause buffer overflows. Some secure alternatives are fgets() and gets_s()",
    score = 4, { cpg =>
      cpg
        .call("gets")
    }
  )

}
