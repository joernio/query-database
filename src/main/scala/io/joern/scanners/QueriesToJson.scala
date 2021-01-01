package io.joern.scanners

import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import org.json4s._
import org.json4s.native.Serialization
import better.files.File
import io.joern.scanners.scan.JoernDefaultArgumentProvider
import io.shiftleft.console.QueryDatabase

object QueriesToJson extends App {
  implicit val engineContext: EngineContext = null
  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)
  val queryDb = new QueryDatabase(new JoernDefaultArgumentProvider())
  File("querydb.json")
    .write(Serialization.write(queryDb.allQueries))
}
