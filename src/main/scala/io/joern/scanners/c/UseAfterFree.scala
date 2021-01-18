package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.codepropertygraph.generated.Operators
import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object UseAfterFree extends QueryBundle {

  @q
  def freeFieldNoReassign()(implicit context: EngineContext): Query = Query(
    name = "free-field-no-reassign",
    author = Crew.fabs,
    title = "A field of a parameter is free'd and not reassigned on all paths",
    description =
      """
        | The function is able to modify a field of a structure passed in by
        | the caller. It frees this field and does not guarantee that on
        | all paths to the exit, the field is reassigned. If any
        | caller now accesses the field, then it accesses memory that is no
        | longer allocated. We also check that the function does not free
        | or clear the entire structure, as in that case, it is unlikely that the
        | passed in structure will be used again.
        |""".stripMargin,
    score = 5.0,
    docStartLine = sourcecode.Line(),
    traversal = { cpg =>
      val freeOfStructField = cpg
        .call("free")
        .where(
          _.argument(1)
            .isCallTo("<operator>.*[fF]ieldAccess.*")
            .filter(x =>
              x.method.parameter.name.toSet.contains(x.argument(1).code))
        )
        .whereNot(_.argument(1).isCall.argument(1).filter { struct =>
          struct.method.ast.isCall
            .name(".*free$", "memset", "bzero")
            .argument(1)
            .codeExact(struct.code)
            .nonEmpty
        })
        .l

      freeOfStructField.argument(1).filter { arg =>
        arg.method.methodReturn.reachableBy(arg).nonEmpty
      }

    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

  @q
  def freeReturnedValue()(implicit context: EngineContext): Query = Query(
    name = "free-returned-value",
    author = Crew.malte,
    title = "A value that is returned through a parameter is free'd in a path",
    description =
      """
        |The function sets a field of a function parameter to a value of a local
        |variable.
        |This variable is then freed in some paths. Unless the value set in the
        |function |parameter is overridden later on, the caller has access to the
        |free'd memory, which is undefined behavior.
        |
        |Finds bugs like CVE-2019-18902.
        |""".stripMargin,
    score = 5.0,
    traversal = { cpg =>
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
        .filter { case (id, freeCall) => freeCall.dominatedBy.exists(_ == id) }
        .flatMap(_._1)
    },
    docEndLine = sourcecode.Line(),
    docFileName = sourcecode.FileName()
  )

}
