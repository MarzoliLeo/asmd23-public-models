package u09.examples

import u09.model.QMatrix

object DesignByQLearning extends App:

  import u09.model.QMatrix.Move.*
  import u09.model.QMatrix.*

  val rl: QMatrix.Facade = Facade(
    width = 10,
    height = 10,
    initial = (0, 0),  // Posizione iniziale
    terminal = { case (9, 9) => true; case _ => false },  // Posizione finale
    reward = {
      case ((9, 9), _) => 100  // Ricompensa per raggiungere la destinazione finale
      case ((5, 5), _) => -50  // Penalità per incontrare un nemico
      case ((x, y), _) if Set((1,1), (1,2), (1,3), (2,1), (3,1)).contains((x, y)) => -100  // Penalità per incontrare ostacoli
      case ((x, y), _) if Set((7,7)).contains((x, y)) => -100  // Penalità per cadere in un buco
      case ((x, y), _) if Set((3, 3), (6, 2)).contains((x, y)) => 10  // Ricompensa per raccogliere oggetti
      case _ => -1  // Penalità standard per ogni passo
    },
    jumps = {
      case ((2, 2), DOWN) => (4, 4)  // Esempio di salto
      case ((6, 3), UP) => (3, 3)    // Un altro salto
    },
    gamma = 0.9,  
    alpha = 0.5,  
    epsilon = 0.9,  
    v0 = 1 
  )

  val q0 = rl.qFunction
  println("Stato iniziale:")
  println(rl.show(q0.vFunction, "%2.2f"))

  val q1 = rl.makeLearningInstance().learn(10000, 100, q0)

  println("Valutazione finale delle politiche:")
  println(rl.show(q1.vFunction, "%2.2f"))

  println("Migliore politica (azioni):")
  println(rl.show(s => q1.bestPolicy(s).toString, "%7s"))
