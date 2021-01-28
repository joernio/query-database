package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.console.{Query, QueryBundle, q}
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.macros.QueryMacros._

object NullTermination extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def strncpyNoNullTerm()(implicit engineContext: EngineContext): Query =
    queryInit(
      "strncpy-no-null-term",
      Crew.fabs,
      "strncpy is used and no null termination is nearby",
      """
        | Upon calling `strncpy` with a source string that is larger
        | than the destination buffer, the destination buffer is not
        | null-terminated by `strncpy` and there is no explicit
        | null termination nearby. This is unproblematic if the
        | buffer size is at least 1 larger than the size passed
        | to `strncpy`.
        |""".stripMargin,
      4, { cpg =>
        val allocations = cpg.method(".*malloc$").callIn.argument(1).l
        cpg
          .method("strncpy")
          .callIn
          .map { c =>
            (c.method, c.argument(1), c.argument(3))
          }
          .filter {
            case (method, dst, size) =>
              dst.reachableBy(allocations).codeExact(size.code).nonEmpty &&
                method.assignments
                  .where(_.target.isArrayAccess.code(s"${dst.code}.*\\[.*"))
                  .source
                  .isLiteral
                  .code(".*0.*")
                  .isEmpty
          }
          .map(_._2)
      },
    ).asInstanceOf[Query]

}
