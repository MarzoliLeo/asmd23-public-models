package scala.u06.examples

import u06.modelling.{MutualExclusion, PetriNet}
import u06.utils.MSet

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.u06.examples.PNReadersWriters.pnRW
import scala.u06.modelling.Boundedness


/*
 * ** TASK 1 **
 */

object PNReadersWriters:

  enum Place:
    case IdleReader, IdleWriter, Reading, Writing, WaitToRead, WaitToWrite

  export Place.*
  export u06.modelling.PetriNet.*
  export u06.modelling.SystemAnalysis.*
  //export u06.modelling.SafetyAnalysis.*
  export u06.utils.MSet

  // DSL-like specification of a Petri Net
  def pnRW = PetriNet[Place](
    MSet(Place.IdleReader) ~~> MSet(Place.Reading),
    MSet(Place.Reading) ~~> MSet(Place.IdleReader),
    MSet(Place.IdleWriter) ~~> MSet(Place.Writing),
    MSet(Place.Writing) ~~> MSet(Place.IdleWriter),

    // Modified transitions to ensure mutual exclusion
    MSet(Place.Writing) ~~> MSet(Place.WaitToWrite) ^^^ MSet(Place.Reading, Place.WaitToRead),  // Write waits if there's a reader or another writer waiting to read
    MSet(Place.Reading) ~~> MSet(Place.WaitToRead) ^^^ MSet(Place.Writing, Place.WaitToWrite),  // Read waits if there's a writer or another writer waiting to write

    // Additional transitions to handle waiting readers and writers
    MSet(Place.WaitToRead) ~~> MSet(Place.Reading) ^^^ MSet(Place.Writing), // WaitToRead can proceed to Reading if no writer is active
    MSet(Place.WaitToWrite) ~~> MSet(Place.Writing) ^^^ MSet(Place.Reading) // WaitToWrite can proceed to Writing if no reader is active
  ).toSystem

@main def mainPNReadersWriters =
  import PNReadersWriters.*

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

  if (violations.isEmpty)
    println("No violations found in paths of length up to 100")
  else
    println(s"Found ${violations.size} violation(s).")
    println(s"They are: ${violations}")

