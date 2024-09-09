import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import u06.modelling.MutualExclusion
import u06.utils.MSet

import scala.concurrent.Await
import scala.u06.examples.PNReadersWriters.*
import scala.u06.modelling.Boundedness
import concurrent.duration.DurationInt

object PNReadersWritersScalaCheck extends Properties("PNReadersWriters") {

  // Define a generator for initial states
  val initialStateGen: Gen[MSet[Place]] = Gen.const(MSet(Place.IdleReader, Place.IdleWriter))

  // Define a generator for paths
  def pathGen(initialState: MSet[Place], depth: Int): Gen[Seq[Path[Place]]] = {
    def paths(depth: Int): Gen[Seq[Path[Place]]] = depth match {
      case 0 => Gen.const(Seq.empty)
      case 1 => Gen.const(Seq(List(initialState.iterator.next())))
      case _ =>
        for {
          previousPaths <- paths(depth - 1)
          nextStates <- Gen.listOfN(previousPaths.size, Gen.oneOf(Place.values.toSeq)) // Replace with actual next states
        } yield for {
          (path, next) <- previousPaths.zip(nextStates)
        } yield path :+ next
    }
    paths(depth)
  }

  // Test property for mutual exclusion and boundedness using lazy evaluation
  property("PN for Readers & Writers should maintain mutual exclusion and boundedness using lazy evaluation for paths") = forAll(initialStateGen) { initialState =>
    val mutualExclusion = MutualExclusion(Place.Writing, Place.Reading)
    val boundedness = Boundedness(10)

    val pathsLazy = pnRW.completePathsLazy(initialState, 10)
    val pathsList = pathsLazy.toList // Convert LazyList to List for checking

    pathsList.forall(path => {
      val hasViolation = path.exists(state => mutualExclusion.isViolated(state) || boundedness.isViolated(state))
      !hasViolation
    })
  }

  // Test property for non-deterministic path exploration
  property("PN for Readers & Writers should maintain mutual exclusion and boundedness using non-deterministic path exploration") = forAll(initialStateGen) { initialState =>
    val mutualExclusion = MutualExclusion(Place.Writing, Place.Reading)
    val boundedness = Boundedness(10)

    val pathsNonDeterministicFut = pnRW.completePathsNonDeterministic(initialState, 10)
    val pathsNonDeterministic = Await.result(pathsNonDeterministicFut, 10.minutes) // Awaiting future results

    pathsNonDeterministic.forall(path => {
      val hasViolation = path.exists(state => mutualExclusion.isViolated(state) || boundedness.isViolated(state))
      !hasViolation
    })
  }
}
