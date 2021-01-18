package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.codepropertygraph.generated._
import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.console._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.semanticcpg.language._
import io.shiftleft.semanticcpg.language.types.structure.MethodParameter
import overflowdb.traversal._

object UninitalizedLocal extends QueryBundle {
  @q
  def uninitLocal()(implicit context: EngineContext): Query = Query(
    name = "uninitialized-local",
    author = Crew.malte,
    title = "A (potentially) uninitialized local is accessed",
    description =
      """
        | The function accesses a local variable that may not get initialized in all paths leading to it.
        |""".stripMargin,
    score = 2.0,
    traversal = { cpg =>
      val assigningMethods = Vector("<operator>\\.assignment.*",
                                    "memset",
                                    "bzero",
                                    "memcpy",
                                    "snprintf",
                                    "fgets")
      val notReadingMethods = assigningMethods ++ Vector(
        "<operator>\\.sizeOf",
        "<operator>\\.addressOf",
        "<operator>\\.(indirectF|f)ieldAccess")

      def hasAssignmentsInAllParents(target: Declaration,
                                     node: CfgNode): Boolean = {
        val root = node.method.cfgFirst.headOption
        val visited = collection.mutable.Set[CfgNode]()
        val known = collection.mutable.Set[CfgNode]()

        def cfgWalk(node: CfgNode): Boolean = {
          visited.add(node)

          var allOk = true
          for (prev <- node.cfgPrev) {

            val found = prev match {
              case prev: Call =>
                prev
                  .to(Traversal)
                  .methodFullName(assigningMethods: _*)
                  .argument(1)
                  .isIdentifier
                  .refsTo
                  .filter(_ == target)
                  .nonEmpty
              case _ => false
            }
            if (!found && allOk && !visited.contains(prev) && !known.contains(
                  prev)) {
              allOk = cfgWalk(prev)
            }
          }

          visited.remove(node)
          if (!root.contains(node) && allOk) {
            known.add(node)
            true
          } else {
            false
          }
        }
        val res = cfgWalk(node)
        res
      }

      cpg.local.referencingIdentifiers
        .filter(
          i =>
            i.argumentIndex != 1 || Traversal
              .from(i)
              .astParent
              .isCall
              .methodFullName(notReadingMethods: _*)
              .isEmpty)
        .filter(r => !hasAssignmentsInAllParents(r.refsTo.head, r))
    }
  )

}
