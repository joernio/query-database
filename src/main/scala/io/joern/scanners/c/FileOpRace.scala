package io.joern.scanners.c

import io.joern.scanners.{Crew, QueryTags}
import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.console._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.semanticcpg.language._
import io.shiftleft.macros.QueryMacros._
import overflowdb.traversal.Traversal

object FileOpRace extends QueryBundle {

  @q
  def fileOperationRace()(implicit context: EngineContext): Query =
    Query.make(
      name = "file-operation-race",
      author = Crew.malte,
      title = "Two file operations on the same path can act on different files",
      description =
        """
        |Two subsequent file operations are performed on the same path. Depending on the permissions
        |on this path, an attacker can exploit a race condition and replace the file or directory
        |the path refers to between these calls.
        |Use file operations based on file descriptor/pointer/handles instead of paths to avoid this issue.
        |""".stripMargin,
      score = 3.0,
      withStrRep({ cpg =>
        val firstParam = Set(
          "open",
          "fopen",
          "creat",
          "access",
          "chmod",
          "readlink",
          "chown",
          "lchown",
          "stat",
          "lstat",
          "unlink",
          "rmdir",
          "mkdir",
          "mknod",
          "mkfifo",
          "chdir",
          "link",
          "rename"
        )
        val secondParam = Set(
          "openat",
          "fstatat",
          "fchmodat",
          "readlinkat",
          "unlinkat",
          "mkdirat",
          "mknodat",
          "mkfifoat",
          "faccessat",
          "link",
          "rename",
          "linkat",
          "renameat"
        )
        val fourthParam = Set("linkat", "renameat")

        val anyParam = firstParam ++ secondParam ++ fourthParam

        def fileCalls(calls: Traversal[Call]) =
          calls.nameExact(anyParam.toSeq: _*)

        def fileArgs(c: Call) = {
          val res = Traversal.newBuilder[Expression]
          // note some functions are in multiple setts because they take multiple paths
          if (firstParam.contains(c.name)) {
            res.addOne(c.argument(1))
          }
          if (secondParam.contains(c.name)) {
            res.addOne(c.argument(2))
          }
          if (fourthParam.contains(c.name)) {
            res.addOne(c.argument(4))
          }
          res.result().whereNot(_.isLiteral)
        }

        fileCalls(cpg.call)
          .filter(call => {
            val otherCalls = fileCalls(call.method.ast.isCall).filter(_ != call)
            val argsForOtherCalls =
              otherCalls.flatMap(c => fileArgs(c)).code.toSet

            fileArgs(call).code.exists(arg => argsForOtherCalls.contains(arg))
          })
      }),
      tags = List(QueryTags.raceCondition, QueryTags.default),
    )

}
