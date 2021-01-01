package io.joern.scanners.c

import io.joern.scanners.{JoernDefaultArgumentProvider, QueryDatabase}

class ScanTests extends Suite {

  "Scan" should {

    "not crash when loading all queries" in {
      new QueryDatabase(new JoernDefaultArgumentProvider()).allQueries.size should be > 0
    }

  }

}
