package io.joern.batteries.c.vulnscan

import io.joern.batteries.lib.finding
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object HeapBasedOverflow {

  /**
    * Identify calls to malloc with arithmetic operations in the first argument where
    * the returned buffer is subsequently used in a memcpy operation and the third
    * argument of memcpy does not contain the same expression as used in the allocation.
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
