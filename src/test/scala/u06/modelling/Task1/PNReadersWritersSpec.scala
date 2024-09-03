package u06.modelling.Task1

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*


class PNReadersWritersSpec extends AnyFunSuite:

  import scala.u06.examples.PNReadersWriters.*

  test("PN for Readers & Writers should maintain mutual exclusion for paths of length up to 100"):

    val initialState = MSet(Place.IdleReader, Place.IdleWriter)
    val paths = pnRW.completePathsUpToDepth(initialState, 7)

    val violations = paths.filter { path =>
      path.exists { marking =>
        val writerCount = marking(Place.Writing)
        val readerCount = marking(Place.Reading)
        writerCount > 1 || (writerCount > 0 && readerCount > 0)
      }
    }

    violations should be (empty)


