package io.joern.scanners.ghidra

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object MainArgsToStrcpy extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def mainArgsToStrcpy()(implicit context: EngineContext): Query =
    Query.make(
      name = "main-args-to-strcpy",
      author = Crew.claudiu,
      title = "`main` fn arguments used in strcpy source buffer",
      description =
        """
        |User-input ends up in source buffer argument of strcpy, which might overflow the destination buffer.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        def source = cpg.method.fullName("main").parameter
        def sink = cpg.call.methodFullName("strcpy").argument
        sink.reachableBy(source).l
      }),
      tags = List(QueryTags.badfn)
    )
}
