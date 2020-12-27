name := "batteries"
ThisBuild/organization := "io.joern"
ThisBuild/scalaVersion := "2.13.0"

enablePlugins(JavaAppPackaging)
enablePlugins(GitVersioning)

lazy val schema = project.in(file("schema"))
dependsOn(schema)
libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "1.2.2",
  "com.github.pathikrit" %% "better-files"             % "3.8.0",
  "com.github.scopt" %% "scopt" % "3.7.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3" % Runtime,
  "io.shiftleft" %% "semanticcpg" % Versions.cpg,
  "io.shiftleft" %% "dataflowengineoss" % Versions.cpg,
  "io.shiftleft" %% "fuzzyc2cpg-tests" % Versions.cpg % Test classifier "tests",
  "io.shiftleft" %% "semanticcpg-tests" % Versions.cpg % Test classifier "tests",
  "io.shiftleft" %% "fuzzyc2cpg" % Versions.cpg % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test
)
excludeDependencies += ExclusionRule("io.shiftleft", "codepropertygraph-domain-classes_2.13")

// We exclude a few jars that the main joern distribution already includes
universalArchiveOptions in (Universal, packageZipTarball) :=
  (Seq("--exclude", "**/org.scala*") ++
    Seq("--exclude", "**/net.sf.trove4*") ++
    Seq("--exclude", "**/com.google.guava*") ++
    Seq("--exclude", "**/org.apache.logging*") ++
    Seq("--exclude", "**/com.google.protobuf*") ++
    (universalArchiveOptions in (Universal, packageZipTarball)).value)

lazy val createDistribution = taskKey[Unit]("Create binary distribution of extension")
createDistribution := {
  (Universal/packageZipTarball).value
  val pkgBin = (Universal/packageZipTarball).value
  val dstArchive = "./query-database.tgz"
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
