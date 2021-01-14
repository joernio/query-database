package io.joern.scanners.c

import io.joern.scanners.Crew
import io.shiftleft.codepropertygraph.generated.nodes._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import overflowdb.traversal.Traversal

object FileOpRace extends QueryBundle {

  @q
  def fileOperationRace()(implicit context: EngineContext): Query = Query(
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
    traversal = { cpg =>
      {

        def fileArgs(calls: Traversal[Call]) =
          calls
            .flatMap(c =>
              c.name match {
                case "open" | "fopen" | "creat" | "access" | "chmod" |
                    "readlink" | "chown" | "lchown" | "stat" | "lstat" |
                    "unlink" | "rmdir" | "mkdir" | "mknod" | "mkfifo" |
                    "chdir" =>
                  c.argument(1)
                case "openat" | "fstatat" | "fchmodat" | "readlinkat" |
                    "unlinkat" | "mkdirat" | "mknodat" | "mkfifoat" |
                    "faccessat" =>
                  c.argument(2)
                case "link" | "rename" =>
                  c.argument(1) ++ c.argument(2)
                case "linkat" | "renameat" =>
                  c.argument(2) ++ c.argument(4)
                case _ => Traversal.empty
            })
            .whereNot(_.isLiteral)

        fileArgs(cpg.call).flatMap(a =>
          fileArgs(a.method.ast.isCall).filter(a2 =>
            a != a2 && a.code == a2.code))
      }
    }
  )

}
