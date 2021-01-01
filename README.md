# Joern Query Database ("Joern-Scan")

This is the central query database for the open-source code analysis
platform [Joern](https://github.com/ShiftLeftSecurity/joern). It has
two purposes:

* It provides the batteries required to turn Joern into a ready-to-run code scanning tool.
* Its queries serve as examples useful for those looking to write their own queries.

The query database is distributed as a standalone library that
includes Joern as a dependency. This means that it is not necessary to
install Joern to make use of the queries in the database.

At the same time, the database is a Joern extension, that is, when
dynamically loaded at startup, its functionality becomes available on
the interactive Joern shell and in Joern scripts.

You can fork this project to build your own custom queries and
scanners or kindly send a PR to to this repo to have them considered
for inclusion in the default distribution.

## Installing and running

The installation script downloads joern and installs it in a sub-directory.
The query database is installed as an extension.

```
./install.sh
```

You can run all queries as follows:

```
./joern-scan --src path/to/code [--param k1=v1,...]
```

For example,

```
mkdir foo
echo "int foo(int a, int b, int c, int d, int e, int f) {}" > foo/foo.c
./joern-scan --src foo
```

runs all queries on the sample code in the directory `foo`, determining that the function `foo`
has too many parameters.

## Database overview

Queries are grouped into bundles. For example, several queries for determining
software metrics are packaged in the bundle `Metrics` in
`src/main/scala/io/joern/scanners/c/Metrics.scala`:

```
object Metrics extends QueryBundle {

  @q
  def tooManyParameters(n: Int = 4): Query = Query(
    title = s"Number of parameters larger than $n",
    description =
      s"This query identifies functions with more than $n formal parameters",
    score = 2.0, { cpg =>
      cpg.method.filter(_.parameter.size > n)
    }
  )

  @q
  def tooHighComplexity(n: Int = 4): Query = Query(
    title = s"Cyclomatic complexity higher than $n",
    description =
      s"This query identifies functions with a cyclomatic complexity higher than $n",
    score = 2.0, { cpg =>
      cpg.method.filter(_.controlStructure.size > n)
    }
  )
  ...
}
```

As you can see, each query is implemented in a function that receives
a code property graph (type `Cpg`) and returns a list of findings
(type `List[nodes.NewFinding]`).

Corresponding tests for queries are located in
`src/test/scala/io/joern/scanners`. For example, tests for the metrics
queries are located in
`src/test/scala/io/joern/scanners/c/MetricsTests.scala`:

```
class MetricsTests extends Suite {

  override val code = """
    int too_many_params(int a, int b, int c, int d, int e) {
    }
	...
	"""

  "find functions with too many parameters" in {
    Metrics.tooManyParameters(4)(cpg).map(_.evidence) match {
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

## Adding queries to existing scripts

You can add queries to existing bundles, simply by defining a method in the bundle class with the
method annotation `@q`. You can also add your own bundles by placing an `object` that extends from
`QueryBundle` directly in or in a sub package of `io.joern.scanners`. Please also add tests
for your queries to ensure that they continue functioning. Tests also serve as a specification
for what your queries should and should not do.
