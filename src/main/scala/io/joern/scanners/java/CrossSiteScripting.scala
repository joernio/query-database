package io.joern.scanners.java;

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.dataflowengineoss.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext

object CrossSiteScripting extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def xssServlet()(implicit context: EngineContext): Query =
    Query.make(
      name = "xss-servlet",
      author = Crew.malte,
      title =
        "Reflected Cross-Site Scripting: Servlet Returns HTTP Input in Response",
      description =
        """
        |A servlet returns a URL parameter as part of the response.
        |
        |Unless the parameter is escaped or validated in-between, this is a reflected XSS vulnerability.
        |""".stripMargin,
      score = 8,
      withStrRep({ cpg =>
        def source =
          // the value returned by the call to getParameter is attacker-controlled
          cpg.call.methodFullNameExact(
            "javax.servlet.http.HttpServletRequest.getParameter:java.lang.String(java.lang.String)"
          )

        def responseWriter =
          // writers that go towards http responses
          cpg.call.methodFullNameExact(
            "javax.servlet.http.HttpServletResponse.getWriter:java.io.PrintWriter()"
          )

        def sinks =
          // format: off
          // write operations where 'this' (argument 0) is a responseWriter
          cpg.call.
            methodFullNameExact(
              "java.io.PrintWriter.println:void(java.lang.String)"
            ).
            where(_.argument(0).reachableBy(responseWriter))
          // format: on

        // sinks where the first argument is reachable by a source
        sinks.where(_.argument(1).reachableBy(source))
      }),
      tags = List(QueryTags.xss)
    )
}
