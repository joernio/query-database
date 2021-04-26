package io.joern.scanners.c

import io.joern.scanners.{Crew, QueryTags}
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._
import QueryLangExtensions._

object RetvalChecks extends QueryBundle {

  @q
  def uncheckedReadRecvMalloc(): Query =
    Query.make(
      name = "unchecked-read-recv-malloc",
      author = Crew.fabs,
      title = "Unchecked read/recv/malloc",
      description =
        """
      |The return value of a read/recv/malloc call is not checked directly and
      |the variable it has been assigned to (if any) does not
      |occur in any check within the caller.
      |""".stripMargin,
      score = 3.0,
      withStrRep({ cpg =>
        implicit val noResolve: NoResolve.type = NoResolve
        cpg
          .method("(?i)(read|recv|malloc)")
          .callIn
          .returnValueNotChecked
      }),
      tags = List(QueryTags.default)
    )

}
