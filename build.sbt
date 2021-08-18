name := "batteries"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.5" 
// don't upgrade to 2.13.6 until https://github.com/com-lihaoyi/Ammonite/issues/1182 is resolved

val cpgVersion = "1.3.287"
val ghidra2cpgVersion = "0.0.24"

enablePlugins(JavaAppPackaging)
enablePlugins(GitVersioning)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "sourcecode" % "0.1.9",
  "com.lihaoyi" %% "upickle" % "1.2.2",
  "com.github.pathikrit" %% "better-files"             % "3.8.0",
  "com.github.scopt" %% "scopt" % "3.7.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3" % Runtime,
  "io.joern" %% "ghidra2cpg" % ghidra2cpgVersion,
  "io.shiftleft" %% "semanticcpg" % cpgVersion,
  "io.shiftleft" %% "console" % cpgVersion,
  "io.shiftleft" %% "dataflowengineoss" % cpgVersion,
  "io.shiftleft" %% "fuzzyc2cpg-tests" % cpgVersion % Test classifier "tests",
  "io.shiftleft" %% "c2cpg-tests" % cpgVersion % Test classifier "tests",
  "io.shiftleft" %% "semanticcpg-tests" % cpgVersion % Test classifier "tests",
  "io.shiftleft" %% "fuzzyc2cpg" % cpgVersion % Test,
  "io.shiftleft" %% "c2cpg" % cpgVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "io.joern" %% "ghidra2cpg-tests" % ghidra2cpgVersion % Test classifier "tests"
)

Compile/doc/sources := Seq.empty
Compile/packageDoc/publishArtifact := false

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
  "-target:jvm-1.8",
)

ThisBuild/javacOptions ++= Seq("-source", "1.8")
ThisBuild/Test/compile/javacOptions ++= Seq("-g", "-target", "1.8")

ThisBuild/licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

Global/onChangedBuildSource := ReloadOnSourceChanges

fork := true

ThisBuild/resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.bintrayRepo("shiftleft", "maven"),
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public")
