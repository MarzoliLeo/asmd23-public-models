package u07.examples

import u07.utils.Time

import java.util.Random
import u07.examples.StochasticChannel.*

import scala.u07.modelling.CTMCAnalysis

@main def mainStochasticChannelSimulation =
  Time.timed:
    println:
      stocChannel.newSimulationTrace(IDLE, new Random)
        .take(10)
        .toList
        .mkString("\n")

@main def runAnalysis =
  val avgTime = CTMCAnalysis.averageTimeToState(stocChannel, IDLE, DONE, 1000)
  println(s"Average time to complete communication: $avgTime ms")

  val failPercent = CTMCAnalysis.failTimePercentage(stocChannel, IDLE, FAIL, DONE, 1000)
  println(s"Percentage of time in FAIL state: $failPercent %")
