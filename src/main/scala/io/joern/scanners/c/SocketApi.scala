package io.joern.scanners.c

import io.joern.scanners.{Crew, QueryTags}
import io.shiftleft.console.{Query, QueryBundle, q}
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import QueryLangExtensions._

object SocketApi extends QueryBundle {

  @q
  def uncheckedSend()(implicit context: EngineContext): Query =
    Query.make(
      name = "socket-send",
      author = Crew.fabs,
      title = "Unchecked call to send",
      description =
        """
          | When calling `send`, the return value must be checked to determine
          | if the send operation was successful and how many bytes were
          | transmitted.
          |""".stripMargin,
      score = 2.0,
       withStrRep({ cpg =>
         implicit val noResolve: NoResolve.type = NoResolve
        cpg.method("send").filter(_.parameter.size == 4)
          .callIn.returnValueNotChecked
      }),
      tags = List(QueryTags.default, QueryTags.posix)
    )

}
