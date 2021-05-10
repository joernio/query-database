package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal._

class CredentialDropTests extends QueryTestSuite {

  override def queryBundle = CredentialDrop

  "strict order of credential dropping function calls should be observed" in {
    queryBundle
      .userCredDrop()(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Call]
      .method
      .name
      .toSet shouldBe Set("bad1", "bad3")

    queryBundle
      .groupCredDrop()(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Call]
      .method
      .name
      .toSet shouldBe Set("bad2")
  }

}
