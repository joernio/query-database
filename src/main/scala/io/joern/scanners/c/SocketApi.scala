package io.joern.scanners.c

import io.joern.scanners.{Crew, QueryTags}
import io.shiftleft.console.{Query, QueryBundle, q}
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.dataflowengineoss.semanticsloader.Semantics
import io.shiftleft.macros.QueryMacros._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import QueryLangExtensions._

object SocketApi extends QueryBundle {

  implicit val engineContext: EngineContext = EngineContext(Semantics.empty)

  @q
  def uncheckedSend(): Query =
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
        cpg
          .method("send")
          .filter(_.parameter.size == 4)
          .callIn
          .returnValueNotChecked
      }),
      tags = List(QueryTags.default, QueryTags.posix),
      codeExamples = CodeExamples(
        List(
          """
          |
          |void return_not_checked(int sockfd, void *buf, size_t len, int flags) {
          |    send(sockfd, buf, len, flags);
          |}
          |
          |""".stripMargin),
        List(
          """
          |
          |void return_checked(int sockfd, void *buf, size_t len, int flags) {
          |    if (send(sockfd, buf, len, flags) <= 0) {
          |        // Do something
          |    }
          |}
          |
          |""".stripMargin,
          """
          |
          |void return_var_checked(int sockfd, void *buf, size_t len, int flags) {
          |    ssize_t ret = send(sockfd, buf, len, flags);
          |
          |    if (ret <= 0) {
          |        // Do something
          |    }
          |}
          |
          |""".stripMargin)
      )

    )
}
