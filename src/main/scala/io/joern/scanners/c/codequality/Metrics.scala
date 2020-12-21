package io.joern.scanners.c.codequality

import io.joern.scanners.lib.finding
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.semanticcpg.language._

object Metrics {

  /**
    * Identify functions that have more than `n` parameters
    * */
  def tooManyParameters(cpg: Cpg, n: Int = 4): List[nodes.NewFinding] = {
    cpg.method
      .filter(_.parameter.size > n)
      .map(
        finding(_,
                title = s"Number of parameters larger than $n",
                description = "-",
                score = 2))
      .l
  }

  /**
    * Identify functions that have a cyclomatic complexity higher than `n`
    * */
  def tooHighComplexity(cpg: Cpg, n: Int = 4): List[nodes.NewFinding] = {
    cpg.method
      .filter(_.controlStructure.size > n)
      .map(
        finding(_,
                title = s"Cyclomatic complexity higher than $n",
                description = "-",
                score = 2))
      .l
  }

  /**
    * Identify functions that are more than `n` lines long
    * */
  def tooLong(cpg: Cpg, n: Int = 1000): List[nodes.NewFinding] = {
    cpg.method
      .filter(_.numberOfLines > n)
      .map(
        finding(_, title = s"More than $n lines", description = "-", score = 2))
      .l
  }

  /**
    * Identify functions with more than one return.
    * */
  def multipleReturns(cpg: Cpg): List[nodes.NewFinding] = {
    cpg.method
      .filter(_.ast.isReturn.l.size > 1)
      .map(
        finding(_, title = s"Multiple returns", description = "-", score = 2))
      .l
  }

  /**
    * Identify functions containing more than `n` loops
    * */
  def tooManyLoops(cpg: Cpg, n: Int = 4): List[nodes.NewFinding] = {
    cpg.method
      .filter(
        _.ast.isControlStructure.parserTypeName("(For|Do|While).*").size > n)
      .map(
        finding(_, title = s"More than $n loops", description = "-", score = 2))
      .l
  }

  /**
    * Identify functions with a nesting level higher than `n`
    * */
  def tooNested(cpg: Cpg, n: Int = 3): List[nodes.NewFinding] = {
    cpg.method
      .filter(_.depth(_.isControlStructure) > n)
      .map(
        finding(_,
                title = s"Nesting level higher than $n",
                description = "-",
                score = 2))
      .l
  }

}
