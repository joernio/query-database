package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.{CodeExamples, Query, QueryBundle, QueryDatabase}
import io.shiftleft.semanticcpg.language._
import io.shiftleft.console.scan._
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.dataflowengineoss.semanticsloader.Semantics

import io.shiftleft.dataflowengineoss.semanticsloader.{Parser, Semantics}

import scala.annotation.unused
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}


object EmptyBundle extends QueryBundle {}

class QueryTestSuite extends Suite {

  def queryBundle: QueryBundle = EmptyBundle

  def allQueries: List[Query] = {
    new QueryDatabase().queriesInBundle(queryBundle.getClass)
  }

  def concatedQueryCodeExamples: String =
    allQueries.map { q =>
      q.codeExamples
        .positive
        .mkString("\n")
        .concat("\n")
        .concat(
          q.codeExamples
            .negative
            .mkString("\n"))
  }.mkString("\n")

  override val code = concatedQueryCodeExamples
}
