package io.joern.suites


import io.joern.ghidra2cpg.fixtures.{DataFlowBinToCpgSuite, GhidraBinToCpgSuite}
import io.joern.util.QueryUtil
import io.shiftleft.console.scan._
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.{Query, QueryBundle}
import io.shiftleft.utils.ProjectRoot
import io.shiftleft.semanticcpg.language._

class GhidraQueryTestSuite extends DataFlowBinToCpgSuite {
  val argumentProvider = new QDBArgumentProvider(3)
  override val binDirectory: String = ProjectRoot.relativise("src/test/resources/testbinaries")

  override def beforeAll(): Unit = {
    semanticsFilename = argumentProvider.testSemanticsFilename
  }

  def queryBundle: QueryBundle = QueryUtil.EmptyBundle

  def allQueries = QueryUtil.allQueries(queryBundle, argumentProvider)

  def findMatchingCalls(query: Query): Set[String] = {
    query(cpg)
      .flatMap(_.evidence)
      .collect { case call: nodes.Call => call }
      .method
      .name
      .toSetImmutable
  }
}
