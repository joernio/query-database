package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.console._

object HeapBasedOverflow extends QueryBundle {

  /**
    * Find calls to malloc where the first argument contains an arithmetic expression,
    * the allocated buffer flows into memcpy as the first argument, and the third
    * argument of that memcpy is unequal to the first argument of malloc. This is
    * an adaption of the old-joern query first shown at 31C3 that found a
    * buffer overflow in VLC's MP4 demuxer (CVE-2014-9626).
    * */
  @q
  def mallocMemcpyIntOverflow()(implicit context: EngineContext): Query = Query(
    name = "malloc-memcpy-int-overflow",
    author = Crew.fabs,
    title = "Dangerous copy-operation into heap-allocated buffer",
    description = "-",
    score = 4,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      val src = cpg
        .call(".*malloc$")
        .where(_.argument(1).arithmetics)
        .l

      cpg
        .call("memcpy")
        .l
        .filter { memcpyCall =>
          memcpyCall
            .argument(1)
            .reachableBy(src)
            .where(_.inAssignment.target.codeExact(memcpyCall.argument(1).code))
            .whereNot(_.argument(1).codeExact(memcpyCall.argument(3).code))
            .hasNext
        }
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

}
