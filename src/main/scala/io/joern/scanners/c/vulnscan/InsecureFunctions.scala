package io.joern.scanners.c.vulnscan

import io.joern.scanners.language._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object InsecureFunctions {

  def getsUsed(): Query = Query(
    title = "Insecure function gets() used",
    description =
      "Avoid gets() function as it can lead to reads beyond buffer boundary and cause buffer overlfows. Some secure alternatives are fgets() and gets_s()",
    score = 4, { cpg =>
      cpg
        .call("gets")
    }
  )

}
