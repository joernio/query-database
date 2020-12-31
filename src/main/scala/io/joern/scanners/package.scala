package io.joern

import scala.annotation.StaticAnnotation

package object scanners {

  trait QueryBundle

  class query() extends StaticAnnotation

}
