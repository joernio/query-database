package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.codepropertygraph.generated.Operators
import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.dataflowengineoss.semanticsloader.Semantics
import io.shiftleft.macros.QueryMacros._
import overflowdb.traversal.Traversal

object UseAfterFree extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def freeFieldNoReassign()(implicit context: EngineContext): Query =
    queryInit(
      "free-field-no-reassign",
      Crew.fabs,
      "A field of a parameter is free'd and not reassigned on all paths",
      """
        | The function is able to modify a field of a structure passed in by
        | the caller. It frees this field and does not guarantee that on
        | all paths to the exit, the field is reassigned. If any
        | caller now accesses the field, then it accesses memory that is no
        | longer allocated. We also check that the function does not free
        | or clear the entire structure, as in that case, it is unlikely that the
        | passed in structure will be used again.
        |""".stripMargin,
      4.0, { cpg =>
        val freeWrapper = cpg
          .method("k?free")
          .callIn
          .flatMap { x =>
            x.method.parameter
              .nameExact(x.argument(1).code)
              .map(y => (x.method, y.order))
              .l
          }
          .l

        val freeAndItsWrappers = freeWrapper ++ cpg
          .method("k?free")
          .map(x => (x, new Integer(1)))

        // Arguments that are passed to free-like functions and free'd by them
        val args = freeAndItsWrappers.flatMap {
          case (method, argIndex) =>
            method.callIn.argument(argIndex).l
        }

        val freeOfStructField = Traversal(args)
        // ensure that a field of an arg is free'd, e.g., free(ptr->foo)
          .where(_.isCallTo("<operator>.*[fF]ieldAccess.*")
            .filter(x =>
              x.method.parameter.name.toSet.contains(x.argument(1).code)))
          // Ensure that there isn't a call to `free(ptr)`
          .whereNot(_.isCall.argument(1).filter { struct =>
            struct.method.ast.isCall
            // TODO ... or any other free-like function
              .name("k?free$", "memset", "bzero")
              .argument(1)
              .codeExact(struct.code)
              .nonEmpty
          })
          .l

        // Ensure that free reaches return, that is, there is no
        // `ptr->foo = NULL` after the free
        freeOfStructField.filter { arg =>
          implicit val s: Semantics = context.semantics
          arg.method.methodReturn.ddgInPathElem.exists { p =>
            p.node == arg && p.outEdgeLabel == arg.code
          }
        }
      },
    )

  @q
  def freeReturnedValue()(implicit context: EngineContext): Query =
    queryInit(
      "free-returned-value",
      Crew.malte,
      "A value that is returned through a parameter is free'd in a path",
      """
        |The function sets a field of a function parameter to a value of a local
        |variable.
        |This variable is then freed in some paths. Unless the value set in the
        |function |parameter is overridden later on, the caller has access to the
        |free'd memory, which is undefined behavior.
        |
        |Finds bugs like CVE-2019-18902.
        |""".stripMargin,
      5.0, { cpg =>
        def outParams =
          cpg.parameter
            .typeFullName(".+\\*")
            .whereNot(
              _.referencingIdentifiers
                .argumentIndex(1)
                .inCall
                .nameExact(Operators.assignment, Operators.addressOf))

        def assignedValues =
          outParams.referencingIdentifiers
            .argumentIndex(1)
            .inCall
            .nameExact(Operators.indirectFieldAccess,
                       Operators.indirection,
                       Operators.indirectIndexAccess)
            .argumentIndex(1)
            .inCall
            .nameExact(Operators.assignment)
            .argument(2)
            .isIdentifier

        def freeAssigned =
          assignedValues
            .map(
              id =>
                (id,
                 id.refsTo
                   .flatMap {
                     case p: MethodParameterIn => p.referencingIdentifiers
                     case v: Local             => v.referencingIdentifiers
                   }
                   .inCall
                   .name("(.*_)?free")))

        freeAssigned
          .filter {
            case (id, freeCall) => freeCall.dominatedBy.exists(_ == id)
          }
          .flatMap(_._1)
      },
    )

  @q
  def freePostDominatesUsage()(implicit context: EngineContext): Query =
    queryInit(
      "free-follows-value-reuse",
      Crew.malte,
      "A value that is free'd is reused without reassignment.",
      """
        |A value is used after being free'd in a path that leads to it
        |without reassignment.
        |
        |Modeled after CVE-2019-18903.
        |""".stripMargin,
      5.0, { cpg =>
        cpg.method
          .name("(.*_)?free")
          .filter(_.parameter.size == 1)
          .callIn
          .where(_.argument(1).isIdentifier)
          .flatMap(f => {
            val freedIdentifierCode = f.argument(1).code
            val postDom = f.postDominatedBy.toSet

            val assignedPostDom = postDom.isIdentifier
              .where(_.inAssignment)
              .codeExact(freedIdentifierCode)
              .flatMap(id => id ++ id.postDominatedBy)

            postDom
              .removedAll(assignedPostDom)
              .isIdentifier
              .codeExact(freedIdentifierCode)
          })
      },
    )

}
