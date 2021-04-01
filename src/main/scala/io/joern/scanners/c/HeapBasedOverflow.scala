package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._

object HeapBasedOverflow extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  /**
    * Find calls to malloc where the first argument contains an arithmetic expression,
    * the allocated buffer flows into memcpy as the first argument, and the third
    * argument of that memcpy is unequal to the first argument of malloc. This is
    * an adaption of the old-joern query first shown at 31C3 that found a
    * buffer overflow in VLC's MP4 demuxer (CVE-2014-9626).
    * */
  @q
  def mallocMemcpyIntOverflow()(implicit context: EngineContext): Query =
    Query.make(
      name = "malloc-memcpy-int-overflow",
      author = Crew.fabs,
      title = "Dangerous copy-operation into heap-allocated buffer",
      description = "-",
      score = 4,
      withStrRep({ cpg =>
        // format: off
        val src = cpg.
          method(".*malloc$").
          callIn.
          where(_.argument(1).arithmetics).l

        cpg.
          method("memcpy").
          callIn.l.
          filter { memcpyCall =>
            memcpyCall.
              argument(1).
              reachableBy(src).
              where(
                _.inAssignment.target.codeExact(memcpyCall.argument(1).code)).
              whereNot(_.argument(1).codeExact(memcpyCall.argument(3).code)).
              hasNext
          }
        // format: on
      }),
      tags = List(QueryTags.integers)
    )

}
