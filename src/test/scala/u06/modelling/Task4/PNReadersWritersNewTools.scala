import org.scalatest.funsuite.AnyFunSuite
import u06.modelling.{MutualExclusion}
import u06.utils.MSet

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.u06.examples.PNReadersWriters.*
import scala.u06.modelling.Boundedness

class PNReadersWritersNewTools extends AnyFunSuite:

  test("PN for Readers & Writers should maintain mutual exclusion and boundedness using lazy evaluation for paths of length up to 100"):

    val initialState = MSet(Place.IdleReader, Place.IdleWriter)
    val visitedMarkings = scala.collection.mutable.Set[MSet[Place]]()

    // Definiamo la proprietà di safety.
    val mutualExclusion = MutualExclusion(Place.Writing, Place.Reading)
    val boundedness = Boundedness(10)

    // Utilizza il caching e la lazy evaluation per generare i percorsi.
    val pathsLazy = pnRW.completePathsLazy(initialState, 10)

    // Visualizza gli stati durante l'analisi
    pathsLazy.zipWithIndex.foreach { case (path, index) =>
      println(s"Path $index: $path")
    }

    // Filtra i percorsi e verifica eventuali violazioni
    val violationFutures = pathsLazy.grouped(500).map { pathGroup =>
      Future {
        pathGroup.filter { path =>
          path.exists(state => mutualExclusion.isViolated(state) || boundedness.isViolated(state))
        }
      }
    }

    // Attende che tutte le verifiche siano completate
    val violations = Await.result(Future.sequence(violationFutures), 10.minutes).flatten

    // Verifica che non ci siano violazioni
    assert(violations.isEmpty, s"Found ${violations.size} violation(s). They are: ${violations}")

  test("PN for Readers & Writers should maintain mutual exclusion and boundedness using non-deterministic path exploration"):

    val initialState = MSet(Place.IdleReader, Place.IdleWriter)

    // Definiamo la proprietà di safety.
    val mutualExclusion = MutualExclusion(Place.Writing, Place.Reading)
    val boundedness = Boundedness(10)

    // Generazione dei percorsi con non-determinismo
    val pathsNonDeterministicFut = pnRW.completePathsNonDeterministic(initialState, 10)

    // Verifica eventuali violazioni di proprietà
    val pathsNonDeterministic = Await.result(pathsNonDeterministicFut, 10.minutes)
    pathsNonDeterministic.zipWithIndex.foreach { case (path, index) =>
      println(s"Non-deterministic Path $index: $path")
    }

    val violations = pathsNonDeterministic.filter { path =>
      path.exists(state => mutualExclusion.isViolated(state) || boundedness.isViolated(state))
    }

    // Verifica che non ci siano violazioni
    assert(violations.isEmpty, s"Found ${violations.size} violation(s). They are: ${violations}")

