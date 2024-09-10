package u07.examples

import u07.modelling.{CTMC, SPN}
import u07.utils.MSet

import java.util.Random
import scala.u07.modelling.CTMCAnalysis

object StochasticReadersWriters extends App:
  enum Place:
    case READING, WRITING, WAITING_READER, WAITING_WRITER, IDLE_READER, IDLE_WRITER

  export Place.*
  export u07.modelling.CTMCSimulation.*
  export u07.modelling.SPN.*


  val spn = SPN[Place](
    // Un lettore inizia a leggere se non ci sono scrittori che scrivono
    Trn(MSet(WAITING_READER), m => 1.0, MSet(READING), MSet(WRITING)),

    // Un lettore finisce di leggere e passa a IDLE_READER dove non compie nessuna azione.
    Trn(MSet(READING), m => m(READING), MSet(IDLE_READER), MSet()),

    // Un lettore IDLE ritorna in attesa di leggere
    Trn(MSet(IDLE_READER), m => 1.0, MSet(WAITING_READER), MSet()),

    // Uno scrittore inizia a scrivere se non ci sono lettori o altri scrittori
    Trn(MSet(WAITING_WRITER), m => 1.0, MSet(WRITING), MSet(READING, WRITING)),

    // Uno scrittore finisce di scrivere e passa a IDLE_WRITER dove non compie nessuna azione.
    Trn(MSet(WRITING), m => 2.0, MSet(IDLE_WRITER), MSet()),

    // Uno scrittore IDLE ritorna in attesa di scrivere
    Trn(MSet(IDLE_WRITER), m => 1.0, MSet(WAITING_WRITER), MSet())
  )

  val initialMarking = MSet(WAITING_READER, WAITING_WRITER)

  val ctmcModel = toCTMC(spn)

  println:
    ctmcModel.newSimulationTrace(initialMarking, new Random)
      .take(20)
      .toList
      .mkString("\n")

