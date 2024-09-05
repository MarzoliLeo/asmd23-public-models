package u06.examples

import u06.modelling.PetriNetWithPriority
import u06.modelling.PetriNetWithPriority.*
import u06.utils.MSet

object PNWithPriorityExample:

  enum Place:
    case P1, P2, P3

  import Place.*

  // Definizione della Petri Net con priorità
  val petriNet = PetriNetWithPriority[Place](
    MSet(P1) ~~> MSet(P2) withPriority 1,       // Transizione con priorità 1
    MSet(P1, P2) ~~> MSet(P3) withPriority 2    // Transizione con priorità 2
  )

  val initialState = MSet(P1)
  val system = petriNet.toSystem

  @main def testPNWithPriority() =
    // Test per la Petri Net con priorità
    println("Testing Petri Net with Priority:")
    val nextStates = system.next(initialState)
    println(s"Initial State: $initialState")
    println(s"Next States: $nextStates")

