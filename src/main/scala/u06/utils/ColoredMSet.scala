package u06.utils

// Coloured Multiset datatype
trait ColoredMSet[A, V] extends ((A, V) => Int):
  def union(m: ColoredMSet[A, V]): ColoredMSet[A, V]
  def diff(m: ColoredMSet[A, V]): ColoredMSet[A, V]
  def disjoined(m: ColoredMSet[A, V]): Boolean
  def size: Int
  def matches(m: ColoredMSet[A, V]): Boolean
  def extract(m: ColoredMSet[A, V]): Option[ColoredMSet[A, V]]
  def asList: List[(A, V)]
  def asMap: Map[(A, V), Int]
  def iterator: Iterator[(A, V)]

object ColoredMSet:
  def apply[A, V](l: (A, V)*): ColoredMSet[A, V] = new ColoredMSetImpl(l.toList)

  private case class ColoredMSetImpl[A, V](asMap: Map[(A, V), Int]) extends ColoredMSet[A, V]:
    def this(list: List[(A, V)]) = this(list.groupBy(identity).map((a, n) => (a, n.size)))

    override val asList =
      asMap.toList.flatMap((a, n) => List.fill(n)(a))

    override def apply(v1: A, v2: V) = asMap.getOrElse((v1, v2), 0)
    override def union(m: ColoredMSet[A, V]) = new ColoredMSetImpl[A, V](asList ++ m.asList)
    override def diff(m: ColoredMSet[A, V]) = new ColoredMSetImpl[A, V](asList diff m.asList)
    override def disjoined(m: ColoredMSet[A, V]) = (asList intersect m.asList).isEmpty
    override def size = asList.size
    override def matches(m: ColoredMSet[A, V]) = extract(m).isDefined
    override def extract(m: ColoredMSet[A, V]) =
      Some(this diff m) filter (_.size == size - m.size)
    override def iterator = asMap.keysIterator
    override def toString = s"{${asList.mkString("|")}}"

