package u06.examples

import u06.modelling.PetriNetWithColoring
import u06.modelling.PetriNetWithColoring.*
import u06.utils.ColoredMSet

object PNWithColoringExample:

  enum Place:
    case P1, P2, P3

  enum Color:
    case Red, Blue

  import Place.*
  import Color.*

  // Definizione della Petri Net con colorazione
  val petriNet = PetriNetWithColoring[Place,Color](
    ColoredMSet(P1 -> Red) ~~> ColoredMSet(P2 -> Blue) withPriority 1, // Transizione con priorità 1
    ColoredMSet(P1 -> Red, P2 -> Blue) ~~> ColoredMSet(P3 -> Red) withPriority 2 // Transizione con priorità 2
  )

  // Stato iniziale
  val initialState = ColoredMSet(P1 -> Red)

  // Creazione del sistema
  val system = petriNet.toSystem

  @main def testPNWithColoring() =
    // Test per la Petri Net con colorazione
    println("Testing Petri Net with Coloring:")
    val nextStates = system.next(initialState)
    println(s"Initial State: $initialState")
    println(s"Next States: $nextStates")
