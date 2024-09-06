package u06.examples

import u06.modelling.PetriNetWithColoring
import u06.modelling.PetriNetWithColoring.*
import u06.utils.ColoredMSet

object PNWithColoringExample:

  enum Place:
    case Idle, Reading, Writing, Queue

  enum Color:
    case Reader, Writer

  import Place.*
  import Color.*

  // Definizione della Petri Net per il problema Readers-Writers
  val petriNet = PetriNetWithColoring[Place, Color](
    // Transizione per iniziare a leggere (lettori possono leggere insieme se non c'è scrittura)
    ColoredMSet(Idle -> Reader) ~~> ColoredMSet(Reading -> Reader) ^^^ ColoredMSet(Writing -> Writer) withPriority 1,
    ColoredMSet(Reading -> Reader) ~~> ColoredMSet(Reading -> Reader) ^^^ ColoredMSet(Writing -> Writer) withPriority 1, // Più lettori possono unirsi se nessuno sta scrivendo

    // Transizione per terminare la lettura (quando tutti i lettori hanno finito, torna a Idle)
    ColoredMSet(Reading -> Reader) ~~> ColoredMSet(Idle -> Reader) withPriority 2,

    // Transizione per mettere uno scrittore in coda se ci sono lettori attivi
    ColoredMSet(Idle -> Writer) ~~> ColoredMSet(Queue -> Writer) ^^^ ColoredMSet(Reading -> Reader) withPriority 3,

    // Transizione per iniziare a scrivere (scrittore può scrivere solo se la risorsa è Idle e non ci sono lettori attivi)
    ColoredMSet(Idle -> Writer) ~~> ColoredMSet(Writing -> Writer) ^^^ ColoredMSet(Reading -> Reader) withPriority 3,

    // Transizione per terminare la scrittura (torna a Idle dopo aver scritto)
    ColoredMSet(Writing -> Writer) ~~> ColoredMSet(Idle -> Writer) withPriority 4,

    // Transizione per passare dalla coda alla scrittura quando tutti i lettori hanno finito
    ColoredMSet(Queue -> Writer) ~~> ColoredMSet(Writing -> Writer) ^^^ ColoredMSet(Reading -> Reader) withPriority 5
  )

  // Stato iniziale: risorsa Idle
  val initialState = ColoredMSet(Idle -> Reader, Idle -> Writer)

  // Creazione del sistema
  val system = petriNet.toSystem

  @main def testReadersWriters() =
    // Test per la Petri Net dei lettori e scrittori
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

    if iteration >= maxDepth then
      println(s"\nSimulation stopped after reaching the maximum depth of $maxDepth iterations.")

