package u06.modelling

import u06.utils.{ColoredMSet, MSet}

object PetriNetWithColoring:
  // Estensione della classe Trn per supportare la colorazione dei token
  case class Trn[P, T](cond: ColoredMSet[P, T], eff: ColoredMSet[P, T], inh: ColoredMSet[P, T], priority: Int)
  type PetriNet[P, T] = Set[Trn[P, T]]
  type Marking[P, T] = ColoredMSet[P, T]

  // Factory per creare una Petri Net con colorazione
  def apply[P, T](transitions: Trn[P, T]*): PetriNet[P, T] = transitions.toSet

  // Factory per creare un sistema
  extension [P, T](pn: PetriNet[P, T])
    def toSystem: System[Marking[P, T]] = m =>
      val sortedTransitions = pn.toSeq.sortBy(-_.priority) // Ordina per priorità decrescente
      (for
        Trn(cond, eff, inh, _) <- sortedTransitions // Scorri le transizioni
        if m disjoined inh                          // Controlla l'inibizione
        out <- m extract cond                       // Rimuovi la precondizione
      yield out union eff).toSet                    // Converte la Seq in Set

  // Sintassi per creare regole di transizione con colorazione
  extension [P, T](self: ColoredMSet[P, T])
    def ~~>(y: ColoredMSet[P, T]) = Trn(self, y, ColoredMSet(), 0) // Priorità predefinita 0

  extension [P, T](self: Trn[P, T])
    def ^^^(z: ColoredMSet[P, T]) = self.copy(inh = z)

  extension [P, T](self: Trn[P, T])
    def withPriority(priority: Int) = self.copy(priority = priority)



