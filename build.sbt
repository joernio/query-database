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

// We exclude a few jars that the main joern distribution already includes
Universal / mappings := (Universal / mappings).value.filterNot {
   case (_, path) => path.contains("org.scala") ||
    path.contains("net.sf.trove4") ||
    path.contains("com.google.guava") ||
    path.contains("org.apache.logging") ||
    path.contains("com.google.protobuf") ||
    path.contains("com.lihaoyi.u") ||
    path.contains("io.shiftleft") ||
    path.contains("org.typelevel") ||
    path.contains("io.undertow") ||
    path.contains("com.chuusai") ||
    path.contains("io.get-coursier") ||
    path.contains("io.circe") ||
    path.contains("net.java.dev") ||
    path.contains("com.github.javaparser") ||
    path.contains("org.javassist") ||
    path.contains("com.lihaoyi.ammonite")
}

sources in (Compile,doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

lazy val createDistribution = taskKey[Unit]("Create binary distribution of extension")
createDistribution := {
  (Universal/packageZipTarball).value
  val pkgBin = (Universal/packageBin).value
  val dstArchive = "./querydb.zip"
  IO.copy(
    List((pkgBin, file(dstArchive))),
    CopyOptions(overwrite = true, preserveLastModified = true, preserveExecutable = true)
  )
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
