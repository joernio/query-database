name := "batteries"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.4"

enablePlugins(JavaAppPackaging)
enablePlugins(GitVersioning)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "sourcecode" % "0.1.9",
  "com.lihaoyi" %% "upickle" % "1.2.2",
  "com.github.pathikrit" %% "better-files"             % "3.8.0",
  "com.github.scopt" %% "scopt" % "3.7.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3" % Runtime,
  "io.shiftleft" %% "semanticcpg" % Versions.cpg,
  "io.shiftleft" %% "console" % Versions.cpg,
  "io.shiftleft" %% "dataflowengineoss" % Versions.cpg,
  "io.shiftleft" %% "fuzzyc2cpg-tests" % Versions.cpg % Test classifier "tests",
  "io.shiftleft" %% "semanticcpg-tests" % Versions.cpg % Test classifier "tests",
  "io.shiftleft" %% "fuzzyc2cpg" % Versions.cpg % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test
)

sources in (Compile,doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

lazy val createDistribution = taskKey[Unit]("Create binary distribution of extension")
createDistribution := {
  val pkgBin = (Universal/packageBin).value
  val tmpDstArchive = "/tmp/querydb.zip"
  val dstArchive = "querydb.zip"
  IO.copy(
    List((pkgBin, file(tmpDstArchive))),
    CopyOptions(overwrite = true, preserveLastModified = true, preserveExecutable = true)
  )

  val f = better.files.File(dstArchive)
  better.files.File.usingTemporaryDirectory("querydb") { dir =>
    better.files.File(tmpDstArchive).unzipTo(dir)
    dir.listRecursively.filter{ x => val name = x.toString
        name.contains("org.scala") ||
        name.contains("net.sf.trove4") ||
        name.contains("com.google.guava") ||
        name.contains("org.apache.logging") ||
        name.contains("com.google.protobuf") ||
        name.contains("com.lihaoyi.u") ||
        name.contains("io.shiftleft") ||
        name.contains("org.typelevel") ||
        name.contains("io.undertow") ||
        name.contains("com.chuusai") ||
        name.contains("io.get-coursier") ||
        name.contains("io.circe") ||
        name.contains("net.java.dev") ||
        name.contains("com.github.javaparser") ||
        name.contains("org.javassist") ||
        name.contains("com.lihaoyi.ammonite")
    }.foreach(x => x.delete())
    dir.zipTo(f)
    better.files.File(tmpDstArchive).delete()
  }

  println(s"created distribution - resulting files: $dstArchive")
}

ThisBuild/Compile/scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
)

ThisBuild/licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

Global/onChangedBuildSource := ReloadOnSourceChanges

ThisBuild/resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.bintrayRepo("shiftleft", "maven"),
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public")
