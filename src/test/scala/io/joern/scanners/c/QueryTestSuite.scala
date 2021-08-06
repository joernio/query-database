package io.joern.scanners.c

import io.shiftleft.console.{DefaultArgumentProvider, Query, QueryBundle, QueryDatabase}
import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import io.shiftleft.dataflowengineoss.semanticsloader.{Parser, Semantics}

import scala.reflect.runtime.universe._

object EmptyBundle extends QueryBundle {}

class QDBArgumentProvider(maxCallDepth: Int)  extends DefaultArgumentProvider {
  def testSemanticsFilename = "src/test/resources/default.semantics"

  override def defaultArgument(method: MethodSymbol, im: InstanceMirror, x: Symbol, i: Int): Option[Any] = {
    if (x.typeSignature.toString.endsWith("EngineContext")) {
      val newsemantics = Semantics.fromList(new Parser().parseFile(testSemanticsFilename))
      val engineContext = EngineContext(newsemantics)
      engineContext.config.maxCallDepth = maxCallDepth
      Some(engineContext)
    } else {
      super.defaultArgument(method, im, x, i)
    }
  }
}

class QueryTestSuite extends Suite {
  val argumentProvider = new QDBArgumentProvider(3)

  override def beforeAll(): Unit = {
    semanticsFilename =  argumentProvider.testSemanticsFilename
    super.beforeAll()
  }

  def queryBundle: QueryBundle = EmptyBundle

  def allQueries: List[Query] = {
    new QueryDatabase(defaultArgumentProvider = argumentProvider).queriesInBundle(queryBundle.getClass)
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
