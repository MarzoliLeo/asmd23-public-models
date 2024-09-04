package u06.modelling

import u06.utils.MSet

/*
 * ** TASK 2 **
 */

// Liveness property checks if a certain transition leads to the desired place eventually
case class Liveness[P](waitPlace: P, goalPlace: P):
  def isViolated(marking: MSet[P]): Boolean = {
    marking(waitPlace) > 0 && marking(goalPlace) == 0
  }

