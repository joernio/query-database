package io.joern

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.{NodeTypes, nodes}
import io.shiftleft.console.Query
import overflowdb.traversal.Traversal
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal._

package object scanners {

  implicit class ScannerStarters(val cpg: Cpg) extends AnyVal {

    def finding: Traversal[nodes.Finding] =
      cpg.graph.nodes(NodeTypes.FINDING).cast[nodes.Finding]

  }

  implicit class QueryWrapper(q: Query) {
    def apply(cpg: Cpg): List[nodes.NewFinding] = {
      q.f(cpg)
        .map(
          finding(_,
                  title = q.title,
                  description = q.description,
                  score = q.score)
        )
        .l
    }
  }

  object FindingKeys {
    val title = "title"
    val description = "description"
    val score = "score"
  }

  implicit class ScannerFindingStep(val traversal: Traversal[nodes.Finding])
      extends AnyRef {

    def title: Traversal[String] = traversal.map(_.title)

    def description: Traversal[String] = traversal.map(_.description)

    def score: Traversal[Double] = traversal.map(_.score)

  }

  implicit class ScannerFindingExtension(val node: nodes.Finding)
      extends AnyRef {

    def title: String = getValue(FindingKeys.title)

    def description: String = getValue(FindingKeys.description)

    def score: Double = getValue(FindingKeys.score).toDouble

    protected def getValue(key: String, default: String = ""): String =
      node.keyValuePairs.find(_.key == key).map(_.value).getOrElse(default)

  }

  def finding(evidence: nodes.StoredNode,
              title: String,
              description: String,
              score: Double): nodes.NewFinding = {
    nodes.NewFinding(
      evidence = List(evidence),
      keyValuePairs = List(
        nodes.NewKeyValuePair(FindingKeys.title, title),
        nodes.NewKeyValuePair(FindingKeys.description, description),
        nodes.NewKeyValuePair(FindingKeys.score, score.toString)
      )
    )
  }

  def outputFindings(cpg: Cpg): Unit = {
    cpg.finding.sortBy(_.score.toInt).foreach { finding =>
      val evidence = finding.evidence.headOption
        .map { e =>
          s"${e.location.filename}:${e.location.lineNumber.getOrElse(0)}:${e.location.methodFullName}"
        }
        .getOrElse("")
      println(s"Result: ${finding.score} : ${finding.title}: $evidence")
    }
  }

}
