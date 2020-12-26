# Joern Query Database

This is the central query database for the open-source code analysis
platform [Joern](https://github.com/ShiftLeftSecurity/joern). It has
two purposes:

* It provides the batteries required to turn Joern into a ready-to-run code scanning tool.
* Its queries serve as examples useful for those looking to write their own queries.

The query database is distributed as a standalone library that
includes Joern as a dependency. This means that it is not necessary to
install Joern to make use of the scanners in the database. Instead,
scanners can be invoked from any JVM-based program - as the automatic
tests included in the database demonstrate.

At the same time, the database is a Joern extension, that is, when
dynamically loaded at startup, its functionality becomes available on
the interactive Joern shell and in Joern scripts.

You can fork this project to build your own custom queries and
scanners or kindly send a PR to to this repo to have them considered
for inclusion in the default distribution.

## Database overview

Each scanner is hosted in a sub package of `io.joern.scanners`, that
is, it is located in a directory in
`src/main/scala/io/joern/scanners`. As an example, let us look into
the `CodeQualityScanner` at `src/main/scala/io/joern/scanners`. The
file `Metrics.scala` contains its queries:


```
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
  ...
}
```

As you can see, each query is implemented in a function that receives
a code property graph (type `Cpg`) and returns a list of findings
(type `List[nodes.NewFinding]`).

These queries are invoked in sequence in `CodeQualityPass` in the file
`CodeQualityScanner.scala`:

```
...
class CodeQualityPass(cpg: Cpg) extends CpgPass(cpg) {
  import Metrics._
  override def run(): Iterator[DiffGraph] = {
    val diffGraph = DiffGraph.newBuilder
    (tooManyParameters(cpg) ++ tooManyLoops(cpg) ++ tooNested(cpg) ++
      tooLong(cpg) ++ tooHighComplexity(cpg) ++ multipleReturns(cpg))
      .foreach(diffGraph.addNode)
    Iterator(diffGraph.build)
  }
...
```
Apart from these query invocations, `CodeQualityScanner.scala` merely
contains boilerplate code that turns the scanner into a Joern extension.

Corresponding tests for queries are located in
`src/test/scala/io/joern/scanners`. For example, tests for the metrics
queries are located in
`src/test/scala/io/joern/scanners/c/codequality/MetricsTests.scala`:

```
class MetricsTests extends Suite {

  override val code = """
    int too_many_params(int a, int b, int c, int d, int e) {
    }
	...
	"""

  "find functions with too many parameters" in {
    Metrics.tooManyParameters(cpg, 4).map(_.evidence) match {
      case List(List(method: nodes.Method)) =>
        method.name shouldBe "too_many_params"
      case _ => fail
    }
  }
  ...
}
```

These tests can be run individually from the IntelliJ IDE during query
development.

## Building/Testing the database

We use the Scala Build Tool (sbt). Please make sure you have sbt
installed. The version does not matter as sbt will fetch the required
version based on the build file (`build.sbt`).

Once `sbt` is installed, you can build and test the database as
follows:

```
sbt test
```

Automatic code formatting can be performed as follows:

```
sbt scalafmt
sbt test:scalafmt
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
