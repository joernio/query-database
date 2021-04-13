package io.joern.scanners.java;

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

// The queries are tied to springframework
object SQLInjection extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def sqlInjection()(implicit context: EngineContext): Query =
    Query.make(
      name = "SQL injection",
      author = Crew.niko,
      title =
        "SQL injection: A parameter is used in an insecure database API call.",
      description =
        """
        |An attacker controlled parameter is used in an insecure database API call.
        |
        |If the parameter is not validated and sanitized, this is a SQL injection.
        |""".stripMargin,
      score = 5,
      withStrRep({ cpg =>
        def source =
          cpg.method
            .where(_.methodReturn.evalType(
              "org.springframework.web.servlet.ModelAndView"))
            .parameter

        def sink = cpg.method.name("query").parameter.order(1)

        // sinks where the first argument is reachable by a source
        sink.reachableBy(source).l
      }),
      tags = List(QueryTags.sqlInjection)
    )
}