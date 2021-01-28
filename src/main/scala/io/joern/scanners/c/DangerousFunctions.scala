package io.joern.scanners.c

import io.joern.scanners._
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console._
import io.shiftleft.macros.QueryMacros._

object DangerousFunctions extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def getsUsed(): Query =
    queryInit(
      "call-to-gets",
      Crew.suchakra,
      "Dangerous function gets() used",
      """
        | Avoid `gets` function as it can lead to reads beyond buffer
        | boundary and cause
        | buffer overflows. Some secure alternatives are `fgets` and `gets_s`.
        |""".stripMargin,
      8, { cpg =>
        cpg.method("gets").callIn
      },
    ).asInstanceOf[Query]

  @q
  def argvUsedInPrintf(): Query =
    queryInit(
      "format-controlled-printf",
      Crew.suchakra,
      "Non-constant format string passed to printf/sprintf/vsprintf",
      """
        | Avoid user controlled format strings like "argv" in printf, sprintf and vsprintf 
        | functions as they can cause memory corruption. Some secure
        | alternatives are `snprintf` and `vsnprintf`.
        |""".stripMargin,
      4, { cpg =>
        cpg
          .method("printf")
          .callIn
          .whereNot(_.argument.order(1).isLiteral) ++
          cpg
            .method("(sprintf|vsprintf)")
            .callIn
            .whereNot(_.argument.order(2).isLiteral)
      },
    ).asInstanceOf[Query]

  @q
  def scanfUsed(): Query =
    queryInit(
      "call-to-scanf",
      Crew.suchakra,
      "Insecure function scanf() used",
      """
        | Avoid `scanf` function as it can lead to reads beyond buffer
        | boundary and cause buffer overflows. A secure alternative is `fgets`.
        |""".stripMargin,
      4, { cpg =>
        cpg.method("scanf").callIn
      },
    ).asInstanceOf[Query]

  @q
  def strcatUsed(): Query =
    queryInit(
      "call-to-strcat",
      Crew.suchakra,
      "Dangerous functions `strcat` or `strncat` used",
      """
        | Avoid `strcat` or `strncat` functions. These can be used insecurely
        | causing non null-termianted strings leading to memory corruption.
        | A secure alternative is `strcat_s`.
        |""".stripMargin,
      4, { cpg =>
        cpg.method("(strcat|strncat)").callIn
      },
    ).asInstanceOf[Query]

  @q
  def strcpyUsed(): Query =
    queryInit(
      "call-to-strcpy",
      Crew.suchakra,
      "Dangerous functions `strcpy` or `strncpy` used",
      """
        | Avoid `strcpy` or `strncpy` function. `strcpy` does not check buffer
        | lengths.
        | A possible mitigation could be `strncpy` which could prevent
        | buffer overflows but does not null-terminate strings leading to
        | memory corruption. A secure alternative (on BSD) is `strlcpy`.
        |""".stripMargin,
      4, { cpg =>
        cpg.method("(strcpy|strncpy)").callIn
      },
    ).asInstanceOf[Query]

  @q
  def strtokUsed(): Query =
    queryInit(
      "call-to-strtok",
      Crew.suchakra,
      "Dangerous function strtok() used",
      """
        | Avoid `strtok` function as it modifies the original string in place
        | and appends a null character after each token. This makes the
        | original string unsafe. Suggested alternative is `strtok_r` with
        | `saveptr`.
        |""".stripMargin,
      4, { cpg =>
        cpg.method("strtok").callIn
      },
    ).asInstanceOf[Query]

  @q
  def getwdUsed(): Query =
    queryInit(
      "call-to-getwd",
      Crew.claudiu,
      "Dangerous function getwd() used",
      """
        | Avoid the `getwd` function, it does not check buffer lengths.
        | Use `getcwd` instead, as it checks the buffer size.
        |""".stripMargin,
      4, { cpg =>
        cpg.method("getwd").callIn
      },
    ).asInstanceOf[Query]

}
