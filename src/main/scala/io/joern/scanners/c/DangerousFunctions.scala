package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._

object DangerousFunctions extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def getsUsed(): Query =
    Query.make(
      name = "call-to-gets",
      author = Crew.suchakra,
      title = "Dangerous function gets() used",
      description =
        """
        | Avoid `gets` function as it can lead to reads beyond buffer
        | boundary and cause
        | buffer overflows. Some secure alternatives are `fgets` and `gets_s`.
        |""".stripMargin,
      score = 8,
      withStrRep({ cpg =>
        // format: off
        cpg.method("gets").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def argvUsedInPrintf(): Query =
    Query.make(
      name = "format-controlled-printf",
      author = Crew.suchakra,
      title = "Non-constant format string passed to printf/sprintf/vsprintf",
      description =
        """
        | Avoid user controlled format strings like "argv" in printf, sprintf and vsprintf 
        | functions as they can cause memory corruption. Some secure
        | alternatives are `snprintf` and `vsnprintf`.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        val printfFns = cpg.method("printf").callIn.whereNot(_.argument.order(1).isLiteral)
        val sprintsFns = cpg.method("(sprintf|vsprintf)").callIn.whereNot(_.argument.order(2).isLiteral)
        (printfFns ++ sprintsFns)
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def scanfUsed(): Query =
    Query.make(
      name = "call-to-scanf",
      author = Crew.suchakra,
      title = "Insecure function scanf() used",
      description =
        """
        | Avoid `scanf` function as it can lead to reads beyond buffer
        | boundary and cause buffer overflows. A secure alternative is `fgets`.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        cpg.method("scanf").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def strcatUsed(): Query =
    Query.make(
      name = "call-to-strcat",
      author = Crew.suchakra,
      title = "Dangerous functions `strcat` or `strncat` used",
      description =
        """
        | Avoid `strcat` or `strncat` functions. These can be used insecurely
        | causing non null-termianted strings leading to memory corruption.
        | A secure alternative is `strcat_s`.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        cpg.method("(strcat|strncat)").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def strcpyUsed(): Query =
    Query.make(
      name = "call-to-strcpy",
      author = Crew.suchakra,
      title = "Dangerous functions `strcpy` or `strncpy` used",
      description =
        """
        | Avoid `strcpy` or `strncpy` function. `strcpy` does not check buffer
        | lengths.
        | A possible mitigation could be `strncpy` which could prevent
        | buffer overflows but does not null-terminate strings leading to
        | memory corruption. A secure alternative (on BSD) is `strlcpy`.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        cpg.method("(strcpy|strncpy)").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def strtokUsed(): Query =
    Query.make(
      name = "call-to-strtok",
      author = Crew.suchakra,
      title = "Dangerous function strtok() used",
      description =
        """
        | Avoid `strtok` function as it modifies the original string in place
        | and appends a null character after each token. This makes the
        | original string unsafe. Suggested alternative is `strtok_r` with
        | `saveptr`.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        cpg.method("strtok").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

  @q
  def getwdUsed(): Query =
    Query.make(
      name = "call-to-getwd",
      author = Crew.claudiu,
      title = "Dangerous function getwd() used",
      description =
        """
        | Avoid the `getwd` function, it does not check buffer lengths.
        | Use `getcwd` instead, as it checks the buffer size.
        |""".stripMargin,
      score = 4,
      withStrRep({ cpg =>
        // format: off
        cpg.method("getwd").callIn
        // format: on
      }),
      tags = List(QueryTags.badfn)
    )

}
