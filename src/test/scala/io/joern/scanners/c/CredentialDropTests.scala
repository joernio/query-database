package io.joern.scanners.c

import io.shiftleft.codepropertygraph.generated.nodes
import io.shiftleft.console.scan._
import io.shiftleft.semanticcpg.language._
import overflowdb.traversal._

class CredentialDropTests extends Suite {

  override val code =
    """
    void good() {
      setgroups();
      setresgid();
      setresuid();
    }
    void bad1() {
      setresuid();
    }
    void bad2() {
      setresgid();
      setresuid();
    }
    void bad3() {
      setgroups();
      setresuid();
    }
    """

  "strict order of credential dropping function calls should be observed" in {
    CredentialDrop
      .userCredDrop()(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Call]
      .method
      .name
      .toSet shouldBe Set("bad1", "bad3")
    CredentialDrop
      .groupCredDrop()(cpg)
      .flatMap(_.evidence)
      .cast[nodes.Call]
      .method
      .name
      .toSet shouldBe Set("bad2")
  }

}
