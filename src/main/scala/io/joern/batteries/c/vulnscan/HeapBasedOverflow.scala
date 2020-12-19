package io.joern.batteries.c.vulnscan

import io.joern.batteries.lib.finding
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object HeapBasedOverflow {

  /**
    * Find calls to malloc where the first argument contains an arithmetic expression,
    * the allocated buffer flows into memcpy as the first argument, and the third
    * argument of that memcpy is unequal to the first argument of malloc. This is
    * an adaption of the old-joern query first shown at 31C3 that found a
    * buffer overflow in VLC's MP4 demuxer (CVE-2014-9626).
    * */
  def mallocMemcpyIntOverflow(cpg: Cpg)(
      implicit context: EngineContext): List[nodes.NewFinding] = {
    val src = cpg.call("malloc").where(_.argument(1).arithmetics)
    cpg
      .call("memcpy")
      .filter { call =>
        call
          .argument(1)
          .reachableBy(src)
          .not(_.argument(1).codeExact(call.argument(3).code))
          .hasNext
      }
      .map(
        finding(_,
                title = "Dangerous copy-operation into heap-allocated buffer",
                description = "-",
                score = 4)
      )
      .l
  }

}
