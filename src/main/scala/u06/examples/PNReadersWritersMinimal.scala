package scala.u06.examples

import u06.modelling.{Liveness, MutualExclusion, PetriNet}
import u06.utils.MSet

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

/*
 * ** TASK 2 **
 */

object PNReadersWritersMinimal:

  enum Place:
    case IdleReader, IdleWriter, Reading, Writing, WaitToRead, WaitToWrite, ReadGranted, WriteGranted

  export Place.*
  export u06.modelling.PetriNet.*
  export u06.modelling.SystemAnalysis.*
  export u06.utils.MSet

  // Definizione del Petri Net minimale
  def pnRWminimal = PetriNet[Place](
    // Transizioni per la lettura
    MSet(Place.IdleReader) ~~> MSet(Place.WaitToRead),
    MSet(Place.WaitToRead) ~~> MSet(Place.Reading) ^^^ MSet(Place.Writing, Place.WriteGranted),
    MSet(Place.Reading) ~~> MSet(Place.IdleReader),

    // Transizioni per la scrittura
    MSet(Place.IdleWriter) ~~> MSet(Place.WaitToWrite),
    MSet(Place.WaitToWrite) ~~> MSet(Place.WriteGranted) ^^^ MSet(Place.Reading),
    MSet(Place.WriteGranted) ~~> MSet(Place.Writing) ^^^ MSet(Place.WaitToWrite),
    MSet(Place.Writing) ~~> MSet(Place.IdleWriter),

    // Garantire che ogni richiesta di lettura sia eventualmente concessa
    MSet(Place.WaitToRead) ~~> MSet(Place.ReadGranted) ^^^ MSet(Place.Writing),
    MSet(Place.ReadGranted) ~~> MSet(Place.Reading),

    // Limitare il numero di scrittori concorrenti a 2
    MSet(Place.WriteGranted, Place.Writing) ~~> MSet(Place.Writing) ^^^ MSet(Place.Reading)
  ).toSystem

@main def mainPNReadersWritersMinimal =
  import PNReadersWritersMinimal.*

  val initialState = MSet(Place.IdleReader, Place.IdleWriter)
  val visitedMarkings = scala.collection.mutable.Set[MSet[Place]]()

  // Proprietà da verificare
  val mutualExclusion = MutualExclusion(Place.Writing, Place.Reading)
  val liveness = Liveness(Place.WaitToRead, Place.Reading)

  val paths = pnRWminimal.completePathsUpToDepthFiltered(initialState, 100, path => {
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
        path.exists(state => mutualExclusion.isViolated(state) || liveness.isViolated(state))
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
