package io.joern.scanners

import org.reflections8.Reflections
import org.reflections8.util.{ClasspathHelper, ConfigurationBuilder}
import scala.jdk.CollectionConverters._
import scala.reflect.runtime.{universe => ru}

class QueryDatabase {

  def allQueryMethods = {
    val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
    new Reflections(
      new ConfigurationBuilder().setUrls(
        ClasspathHelper.forPackage("io.joern.scanners",
          ClasspathHelper.contextClassLoader(),
          ClasspathHelper.staticClassLoader()))
    ).getSubTypesOf(classOf[QueryBundle])
      .asScala
      .toList.flatMap{ bundle =>
        val bundleType = runtimeMirror.classSymbol(bundle).toType
        val methods = bundleType.members
          .collect{ case m if m.isMethod => m.asMethod }
          .filter { m => m.annotations.map(_.tree.tpe.typeSymbol.name.toString).contains("query") }

        val im = runtimeMirror.reflect(runtimeMirror.reflectModule(bundleType.typeSymbol.asClass.module.asModule).instance)
        methods.map{ m => ( bundleType, im.reflectMethod(m)) }
      }
  }

}
