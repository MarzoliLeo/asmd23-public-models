package scala.u07.modelling

import u07.modelling.CTMC
import u07.modelling.CTMCSimulation.Trace
import u07.modelling.CTMCSimulation.newSimulationTrace
import u07.utils.Time

import java.util.Random

/*
 * TASK 1.
 */

object CTMCAnalysis:

  def simulate[S](ctmc: CTMC[S], s0: S, runs: Int, stopCondition: S => Boolean, stateAnalysis: Trace[S] => Double): Double = 
    Time.timed:
      val rnd = new Random()
      val results = for {
        _ <- 1 to runs
        trace = ctmc.newSimulationTrace(s0, rnd).takeWhile(e => !stopCondition(e.state))
      } yield stateAnalysis(trace)
      results.sum / runs
  
  def averageTimeToState[S](ctmc: CTMC[S], s0: S, targetState: S, runs: Int): Double =
    simulate(ctmc, s0, runs, _ == targetState, trace => trace.last.time)
  
  def failTimePercentage[S](ctmc: CTMC[S], s0: S, failState: S, targetState: S, runs: Int): Double =
    simulate(ctmc, s0, runs, _ == targetState, trace => {
      val totalTime = trace.last.time
      val failTime = trace.filter(_.state == failState).map(_.time).sum
      (failTime / totalTime) * 100
    })

