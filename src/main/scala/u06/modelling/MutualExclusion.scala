package u06.modelling

import scala.u06.examples.PNReadersWriters.*
import u06.utils.MSet

/*
 * ** TASK 1 **
 */
case class MutualExclusion() extends ModelProperties[MSet[Place]]:
  def isViolated(state: MSet[Place]): Boolean =
    val writerCount = state(Place.Writing)
    val readerCount = state(Place.Reading)
    writerCount > 1 || (writerCount > 0 && readerCount > 0)

