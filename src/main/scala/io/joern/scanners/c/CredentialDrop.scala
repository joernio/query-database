package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.console._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.macros.QueryMacros._

object CredentialDrop extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def userCredDrop(): Query =
    queryInit(
      "setuid-without-setgid",
      Crew.malte,
      "Process user ID is changed without changing groups first",
      """
        |The set*uid system calls do not affect the groups a process belongs to. However, often
        |there exists a group that is equivalent to a user (e.g. wheel or shadow groups are often
        |equivalent to the root user).
        |Group membership can only be changed by the root user.
        |Changes to the user should therefore always be preceded by calls to set*gid and setgroups,
        |""".stripMargin,
      2, { cpg =>
        cpg
          .method("set(res|re|e|)uid")
          .callIn
          .whereNot(_.dominatedBy.isCall.name("set(res|re|e|)?gid"))
      }
    ).asInstanceOf[Query]

  @q
  def groupCredDrop(): Query =
    queryInit(
      "setgid-without-setgroups",
      Crew.malte,
      "Process group membership is changed without setting ancillary groups first",
      """
        |The set*gid system calls do not affect the ancillary groups a process belongs to.
        |Changes to the group membership should therefore always be preceded by a call to setgroups.
        |Otherwise the process may still be a secondary member of the group it tries to disavow.
        |""".stripMargin,
      2, { cpg =>
        cpg
          .method("set(res|re|e|)gid")
          .callIn
          .whereNot(_.dominatedBy.isCall.name("setgroups"))
      }
    ).asInstanceOf[Query]

}
