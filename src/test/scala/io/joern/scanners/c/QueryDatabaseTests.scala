package io.joern.scanners.c

import io.joern.scanners.QueryDatabase
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QueryDatabaseTests extends AnyWordSpec with Matchers {

  "QueryDatabase" should {

    "contain Metrics bundle" in {
      new QueryDatabase().allBundles.count { bundle =>
        bundle.getName == "io.joern.scanners.c.Metrics$"
      } shouldBe 1
    }

    "contain `tooManyParameters` query" in {
      val qdb = new QueryDatabase()
      val metricsBundles = qdb.allBundles.filter { bundle =>
        bundle.getName == "io.joern.scanners.c.Metrics$"
      }
      metricsBundles.size shouldBe 1
      val metricsBundle = metricsBundles.head
      val queries = qdb.queriesInBundle(metricsBundle)
      queries.count(_.title == s"Number of parameters larger than 4") shouldBe 1
    }

  }

}
