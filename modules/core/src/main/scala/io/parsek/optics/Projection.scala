package io.parsek.optics

import io.parsek.PValue.{PArray, PMap}
import io.parsek.implicits._
import io.parsek.{PResult, PValue}

import scala.language.implicitConversions

/**
  * @author Andrei Tupitcyn
  */
case class Projection(lenses: Iterable[(Symbol, Getter[PValue, PValue])]) extends Getter[PValue, PValue] {

  override def get(s: PValue): PResult[PValue] = {
    lenses
      .map {
        case (toKey, lens) => lens
          .get(s)
          .map {
            case PMap(m) if m == Map.empty[Symbol, PValue] => toKey -> PValue.Null
            case PArray(arr) if arr == Vector.empty[PValue] => toKey -> PValue.Null
            case pv: PValue => toKey -> pv
          }
      }
      .filter(_
        .map(kv=> kv._2 != PValue.Null)
        .getOrElse(true)
      )
      .toPResult
      .map(PValue.fromFields)
  }
}

object Projection {
  def apply(lens: (Symbol, Getter[PValue, PValue])*): Projection = Projection(lens)
}
