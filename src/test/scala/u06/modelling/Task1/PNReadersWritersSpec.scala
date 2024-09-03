package u06.modelling.Task1

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.funsuite.AnyFunSuite
import u06.modelling.MutualExclusion

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import u06.utils.MSet

import scala.u06.examples.PNReadersWriters.*
import scala.u06.modelling.Boundedness

class PNReadersWritersSpec extends AnyFunSuite:

  test("PN for Readers & Writers should maintain mutual exclusion and boundedness for paths of length up to 100"):

    val initialState = MSet(Place.IdleReader, Place.IdleWriter)
    val visitedMarkings = scala.collection.mutable.Set[MSet[Place]]()

    // Definiamo la proprietà di safety.
    val mutualExclusion = MutualExclusion()
    val boundedness = Boundedness(10)

    val paths = pnRW.completePathsUpToDepthFiltered(initialState, 100, path => {
      val lastMarking = path.last
      if (visitedMarkings.contains(lastMarking)) false
      else {
        visitedMarkings += lastMarking
        true
      }
    })

    val violationFutures = paths.grouped(500).map { pathGroup =>
      Future {
        pathGroup.filter { path =>
          path.exists(state => mutualExclusion.isViolated(state) || boundedness.isViolated(state))
        }
      }
    }

    // Attende che tutte le verifiche siano completate
    val violations = Await.result(Future.sequence(violationFutures), 10.minutes).flatten

    assert(violations.isEmpty, s"Found ${violations.size} violation(s). They are: ${violations}")



