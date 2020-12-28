package io.joern.scanners.language

import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.{NodeTypes, nodes}
import overflowdb.traversal._

class ScannerStarters(val cpg: Cpg) extends AnyVal {

  def finding: Traversal[nodes.Finding] =
    cpg.graph.nodes(NodeTypes.FINDING).cast[nodes.Finding]

}
