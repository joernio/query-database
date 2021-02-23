package io.joern.scanners.java

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._

object DangerousFunctions extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def execUsed(): Query =
    queryInit(
      "call-to-exec",
      Crew.niko,
      "Dangerous function 'java.lang.Runtime.exec:java.lang.Process(java.lang.String)' used",
      """
        | A call to the function `java.lang.Runtime.exec:java.lang.Process(java.lang.String)` 
        | could result in a potential remote code execution.
        |""".stripMargin,
      8, { cpg =>
        cpg
          .method("java.lang.Runtime.exec:java.lang.Process(java.lang.String)")
          .callIn
      },
    )
}
