# Joern Query Database

This repository functions as a central query database for the
open-source code analysis platform
[Joern](https://github.com/ShiftLeftSecurity/joern). Its purpose is
two-fold: one the one hand, these queries are the batteries required
to turn Joern into a ready-to-run code scanning tool that does not
require prior knowledge in static analysis for successful
operation. On the other hand, the queries serve as examples that may
be useful for those looking to write their own queries.

The query database is distributed as a standalone library that
includes Joern as a dependency. This means that it is not necessary to
install Joern to make use of the scanners in the database. Instead,
scanners can be invoked from any JVM-based program as the automatic
tests included in the database demonstrate.

At the same time, the database is a Joern extension, that is, when
dynamically loaded at startup, its functionality becomes available on
the interactive Joern shell and in Joern scripts.

You can fork this project to build your own custom queries and
scanners or kindly send a PR to to this repo to have them considered
for inclusion in the default distribution.

## Database overview

## Building/Testing the database

We use the Scala Build Tool (sbt). Please make sure you have sbt
installed. The version does not matter as sbt will fetch the required
version based on the build file (`build.sbt`).

Once `sbt` is installed, you can build and test the database as
follows:

```
sbt test
```

## Installation as a Joern Extension

Make sure Joern is installed, then run

```
./install.sh
```

This will install the following scanners:

* cvulnscanner - a vulnerability scanner for C code
* codequalityscanner - a code quality scanner for C code

## Running scanners

You can run scanners as follows:

```
joern --src path/to/code --run <scannername> --param k1=v1,...
```

For example,

```
joern --src path/to/code --run cvulnscanner
```

runs the C vulnerability scanner on the code at `path/to/code`.

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
