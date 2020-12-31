package io.joern.scanners.c

import io.joern.scanners.QueryDatabase
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}

class QueryDatabaseTests extends AnyWordSpec with Matchers {

  "QueryDatabase" should {

    "return all queries" in {
      new QueryDatabase().allQueryMethods
        .foreach{ case (bundleType, x) =>
          val method = x.symbol

          val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
          val im = runtimeMirror.reflect(runtimeMirror.reflectModule(bundleType.typeSymbol.asClass.module.asModule).instance)
          val typeSignature = im.symbol.typeSignature

          // If there is an arg, then we assume it has a default

          val defaultArgMethods = (for (ps <- method.paramLists; p <- ps) yield p).zipWithIndex
            .map{ case (_, i) => s"${method.name}$$default$$${i+1}" }
            .map{name => typeSignature.member(TermName(name)).asMethod }
            .map(m => im.reflectMethod(m))

          val args = defaultArgMethods.map{ m => m.apply() }
          println(x.apply(args :_*))
        }
    }

  }

}
