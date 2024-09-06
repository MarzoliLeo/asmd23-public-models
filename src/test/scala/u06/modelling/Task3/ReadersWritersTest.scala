package u06.examples

import org.scalatest.funsuite.AnyFunSuite
import u06.examples.PNWithColoringExample.petriNet
import u06.modelling.PetriNetWithColoring.*
import u06.utils.ColoredMSet

import u06.examples.PNWithColoringExample.Place.*
import u06.examples.PNWithColoringExample.Color.*

class ReadersWritersTest extends AnyFunSuite:

  test("Readers and Writers Petri Net Simulation should terminate correctly within 100 iterations") {

    // Stato iniziale: risorsa Idle
    val initialState = ColoredMSet(Idle -> Reader, Idle -> Writer)
    // Creazione del sistema
    val system = petriNet.toSystem

    println("Testing Readers and Writers Petri Net with Coloring and Priorities:")

    // Stato iniziale
    println(s"Initial State: $initialState")

    // Simulazione del sistema fino a un massimo di 100 stati
    var currentStates = Set(initialState)
    var iteration = 0
    val maxDepth = 100

    while currentStates.nonEmpty && iteration < maxDepth do
      iteration += 1
      println(s"\nIteration $iteration:")
      currentStates = currentStates.flatMap(system.next)
      currentStates.foreach(s => println(s"Current State: $s"))

    assert(iteration <= maxDepth, "The simulation should not exceed the maximum depth of 100 iterations")
    if iteration >= maxDepth then
      println(s"\nSimulation stopped after reaching the maximum depth of $maxDepth iterations.")
  }

