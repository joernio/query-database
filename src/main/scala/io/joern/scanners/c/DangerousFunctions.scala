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
        cpg.method("(?i)gets").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        val printfFns = cpg
          .method("(?i)printf")
          .callIn
          .whereNot(_.argument.order(1).isLiteral)
        val sprintsFns = cpg
          .method("(?i)(sprintf|vsprintf)")
          .callIn
          .whereNot(_.argument.order(2).isLiteral)
        (printfFns ++ sprintsFns)
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        cpg.method("(?i)scanf").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        cpg.method("(?i)(strcat|strncat)").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        cpg.method("(?i)(strcpy|strncpy)").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        cpg.method("(?i)strtok").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
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
        cpg.method("(?i)getwd").callIn
      }),
      tags = List(QueryTags.badfn, QueryTags.default)
    )

}
