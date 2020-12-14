# Joern Default Scripts

## Installation

Make sure Joern is installed, then run

```
./install.sh
```

This will install the following scripts:

* cscanner - a Vulnerability scanner for C code

## Running Scripts

You can run a script as follows:

```
joern --src path/to/code --run <bundlename> --param k1=v1,...
```

For example,

```
joern --src path/to/code --run cscanner
```

runs the C scanner on the code at `path/to/code`.

## Adding queries to existing scripts

You can add queries to an existing bundles by creating a new query set
in the script package. For example, query sets for the C scanner can
be placed here:

https://github.com/joernio/batteries/blob/main/src/main/scala/io/joern/batteries/c/vulnscan/

The file [`SampleQuerySet.scala`](https://github.com/joernio/batteries/blob/main/src/main/scala/io/joern/batteries/c/vulnscan/SampleQuerySet.scala) serves as a template.

```
object SampleQuerySet {

  def myQuery1(cpg: Cpg): List[nodes.NewFinding] = {
    // Add your query here
  }

  def myQuery2(cpg: Cpg): List[nodes.NewFinding] = {
    // Add another query here
  }
  // ...
}

class SampleQuertSet(cpg: Cpg) extends CpgPass(cpg) {
  import SampleQuerySet._

  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    // Execute queries
    myQuery1(cpg).foreach(diffGraph.addNode)
    myQuery2(cpg).foreach(diffGraph.addNode)

    Iterator(diffGraph.build)
  }
```

Finally, add
a `runPass` line to the script [here](https://github.com/joernio/batteries/blob/main/src/main/scala/io/joern/batteries/c/vulnscan/CScanner.scala#L23):

```
class CScanner(options: CScannerOptions) extends LayerCreator {
  override val overlayName: String = CScanner.overlayName
  override val description: String = CScanner.description

  override def create(context: LayerCreatorContext,
                      storeUndoInfo: Boolean): Unit = {
    runPass(new IntegerTruncations(context.cpg), context, storeUndoInfo)
    // add more `runPass` calls to execute query sets by default
  }
```

## Adding Tests

Please add tests for your queries to ensure that they continue functioning.
Tests also serve as a specification for what your queries should and should not do.

A template for an automated query set test can be found [here](https://github.com/joernio/batteries/blob/main/src/test/scala/io/joern/batteries/c/vulnscan/SampleQuerySetTests.scala)

```
package io.joern.batteries.c.vulnscan

class SampleQuerySetTests extends Suite {

  override val code: String =
    """
       void place_your_code_here() {}
    """

  "find ..." in {
    // test code goes here
  }

}
```
