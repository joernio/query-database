package io.joern.scanners

import io.shiftleft.dataflowengineoss.queryengine.EngineContext
import org.json4s._
import org.json4s.native.Serialization
import better.files.File
import io.shiftleft.console.{DefaultArgumentProvider, QueryDatabase}
import scala.reflect.runtime.universe._

class JoernDefaultArgumentProvider(implicit context: EngineContext)
    extends DefaultArgumentProvider {
  override def defaultArgument(method: MethodSymbol,
                               im: InstanceMirror,
                               x: Symbol,
                               i: Int): Option[Any] = {
    if (x.typeSignature.toString.endsWith("EngineContext")) {
      Some(context)
    } else {
      super.defaultArgument(method, im, x, i)
    }
  }
}

object QueriesToJson extends App {
  implicit val engineContext: EngineContext = null
  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)
  val queryDb = new QueryDatabase(new JoernDefaultArgumentProvider())
  File("querydb.json")
    .write(Serialization.write(queryDb.allQueries))
}
