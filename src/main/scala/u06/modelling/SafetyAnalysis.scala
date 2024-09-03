/*package scala.u06.modelling

import scala.collection.immutable.{LazyList, List, Seq}

// Basical analysis helpers
object SafetyAnalysis:

  type Path[S] = List[S]

  extension [S](system: System[S])

    def normalForm(s: S): Boolean = system.next(s).isEmpty

    def complete(p: Path[S]): Boolean = normalForm(p.last)

    // paths of exactly length `depth`
    def pathsFiltered(s: S, depth: Int, filter: Path[S] => Boolean): Seq[Path[S]] = depth match
      case 0 => LazyList()
      case 1 => LazyList(List(s))
      case _ =>
        for
          path <- pathsFiltered(s, depth - 1, filter)
          next <- system.next(path.last) if filter(path :+ next)
        yield path :+ next

    def completePathsUpToDepthFiltered(s: S, depth: Int, filter: Path[S] => Boolean): Seq[Path[S]] =
      (1 to depth).to(LazyList) flatMap (pathsFiltered(s, _, filter)) filter (complete(_)) */

