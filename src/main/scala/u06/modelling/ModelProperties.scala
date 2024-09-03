package u06.modelling

/*
 * ** TASK 1 **
 */

//This is just one of the properties... you can test any you want.
trait ModelProperties[S]:
  def isViolated(state: S): Boolean

