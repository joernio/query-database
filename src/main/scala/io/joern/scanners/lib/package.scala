package io.joern.scanners

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.{NodeTypes, nodes}
import overflowdb.traversal._

package object lib {

  object FindingKeys {
    val title = "title"
    val description = "description"
    val score = "score"
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

  def outputFindings(cpg : Cpg) : Unit = {
    cpg.graph.nodes(NodeTypes.FINDING).cast[nodes.Finding].foreach(println)
  }

}
