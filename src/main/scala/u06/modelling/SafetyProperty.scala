package u06.modelling

//This is just one of the properties... you can test any you want.
trait SafetyProperty[S]:
  def isViolated(state: S): Boolean

