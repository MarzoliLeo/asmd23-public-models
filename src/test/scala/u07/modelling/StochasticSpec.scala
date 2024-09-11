package u07.utils

import org.scalacheck.Prop.*
import org.scalacheck.{Gen, Properties}

import scala.util.Random
import u07.utils.Stochastics

object StochasticSpec extends Properties("Stochastics"):

  // Test with a fixed seed to ensure repeatability
  given Random = new Random(12345L)

  // Example set of choices with known probabilities
  val choices: Set[(Double, String)] = Set(
    (0.5, "a"),
    (0.3, "b"),
    (0.2, "c")
  )
  
  property("statistics function generates expected proportions") = {
    val result = Stochastics.statistics(choices, 1000)

    if (choices.isEmpty) {
      result.isEmpty
    } else {
      val total = result.values.sum
      val propA = result.getOrElse("a", 0).toDouble / total
      val propB = result.getOrElse("b", 0).toDouble / total
      val propC = result.getOrElse("c", 0).toDouble / total

      // Check that proportions are within a small margin of error
      (propA >= 0.45 && propA <= 0.55) :| s"Proportion of 'a' was $propA" &&
        (propB >= 0.25 && propB <= 0.35) :| s"Proportion of 'b' was $propB" &&
        (propC >= 0.15 && propC <= 0.25) :| s"Proportion of 'c' was $propC"
    }
  }


