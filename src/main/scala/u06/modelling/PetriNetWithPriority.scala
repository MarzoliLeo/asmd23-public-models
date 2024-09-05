package u06.modelling

import u06.utils.MSet

object PetriNetWithPriority:
  // Estensione della classe Trn per includere la priorità
  case class Trn[P](cond: MSet[P], eff: MSet[P], inh: MSet[P], priority: Int)
  type PetriNet[P] = Set[Trn[P]]
  type Marking[P] = MSet[P]

  // Factory per creare una Petri Net con priorità
  def apply[P](transitions: Trn[P]*): PetriNet[P] = transitions.toSet

  // Factory per creare un sistema
  extension [P](pn: PetriNet[P])
    def toSystem: System[Marking[P]] = m =>
      val sortedTransitions = pn.toSeq.sortBy(-_.priority) // Ordina per priorità decrescente
      (for
        Trn(cond, eff, inh, _) <- sortedTransitions // Scorri le transizioni
        if m disjoined inh                          // Controlla l'inibizione
        out <- m extract cond                       // Rimuovi la precondizione
      yield out union eff).toSet                    // Converte la Seq in Set

  // Sintassi per creare regole di transizione
  extension [P](self: Marking[P])
    def ~~>(y: Marking[P]) = Trn(self, y, MSet(), 0) // Priorità predefinita 0

  extension [P](self: Trn[P])
    def ^^^(z: Marking[P]) = self.copy(inh = z)

  extension [P](self: Trn[P])
    def withPriority(priority: Int) = self.copy(priority = priority)


