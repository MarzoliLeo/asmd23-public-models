package scala.u06.modelling

import u06.modelling.ModelProperties
import u06.utils.MSet

import scala.u06.examples.PNReadersWriters.*

case class Boundedness(maxTokens: Int) extends ModelProperties[MSet[Place]]:
  def isViolated(state: MSet[Place]): Boolean =
    state.asMap.values.exists(_ > maxTokens)

