package u06.modelling

import u06.utils.MSet

/*
 * ** TASK 1 **
 */

case class MutualExclusion[P](writing: P, reading: P) extends ModelProperties[MSet[P]]:
  def isViolated(state: MSet[P]): Boolean =
    val writerCount = state(writing)
    val readerCount = state(reading)
    writerCount > 1 || (writerCount > 0 && readerCount > 0)