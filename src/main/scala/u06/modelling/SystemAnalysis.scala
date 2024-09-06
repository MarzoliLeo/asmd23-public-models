package u06.modelling


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Basical analysis helpers
object SystemAnalysis:

  type Path[S] = List[S]

  extension [S](system: System[S])

    def normalForm(s: S): Boolean = system.next(s).isEmpty

    def complete(p: Path[S]): Boolean = normalForm(p.last)

    // paths of exactly length `depth`
    def paths(s: S, depth: Int): Seq[Path[S]] = depth match
      case 0 => LazyList()
      case 1 => LazyList(List(s))
      case _ =>
        for
          path <- paths(s, depth - 1)
          next <- system.next(path.last)
        yield path :+ next

    // complete paths with length '<= depth' (could be optimised)
    def completePathsUpToDepth(s: S, depth:Int): Seq[Path[S]] =
      (1 to depth).to(LazyList) flatMap (paths(s, _)) filter (complete(_))

    /*
     * ** TASK 1 **
     */

    //(This permits to handle 100 states).
    def pathsFiltered(s: S, depth: Int, filter: Path[S] => Boolean): Seq[Path[S]] = depth match
      case 0 => LazyList()
      case 1 => LazyList(List(s))
      case _ =>
        for
          path <- pathsFiltered(s, depth - 1, filter)
          next <- system.next(path.last) if filter(path :+ next)
        yield path :+ next

    def completePathsUpToDepthFiltered(s: S, depth: Int, filter: Path[S] => Boolean): Seq[Path[S]] =
      (1 to depth).to(LazyList) flatMap (pathsFiltered(s, _, filter)) filter (complete(_))


    /*
     * ** TASK 4 **
     */

    // Lazy evaluation with caching of paths
    /*
         * Lazy evaluation of paths with caching. Only evaluates paths as needed,
         * and uses memoization to store already computed paths.
    */

    // Recursive memoization helper with explicitly typed empty cache
    def lazyPaths(s: S, depth: Int, cache: Map[(S, Int), LazyList[Path[S]]] = Map.empty[(S, Int), LazyList[Path[S]]]): (LazyList[Path[S]], Map[(S, Int), LazyList[Path[S]]]) =
    cache.get((s, depth)) match
      case Some(paths) => (paths, cache) // Return cached paths if present
      case None =>
        val newPaths = depth match
          case 0 => LazyList()
          case 1 => LazyList(List(s))
          case _ =>
            for
              path <- lazyPaths(s, depth - 1, cache)._1
              next <- system.next(path.last).to(LazyList)
            yield path :+ next
        val updatedCache: Map[(S, Int), LazyList[Path[S]]] = cache + ((s, depth) -> newPaths)
        (newPaths, updatedCache)

    // Complete paths using lazy evaluation and caching
    def completePathsLazy(s: S, depth: Int): LazyList[Path[S]] =
      val (paths, _) = lazyPaths(s, depth)
      paths.filter(complete)


    /*
     * Non-deterministic path exploration. Uses `List` or `LazyList` as a monad
     * to capture the branching structure of the paths.
     */
    def nonDeterministicPaths(s: S, depth: Int): Future[Seq[Path[S]]] = depth match {
      case 0 => Future.successful(Seq.empty)
      case 1 => Future.successful(Seq(List(s)))
      case _ =>
        for {
          previousPaths <- nonDeterministicPaths(s, depth - 1)
          paths = for {
            path <- previousPaths
            next <- system.next(path.last)
          } yield path :+ next
        } yield paths
    }

    // Complete paths with non-determinism
    def completePathsNonDeterministic(s: S, depth: Int): Future[Seq[Path[S]]] =
      nonDeterministicPaths(s, depth).map(_.filter(complete))



