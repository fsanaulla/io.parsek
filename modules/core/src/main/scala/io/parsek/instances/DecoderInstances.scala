package io.parsek.instances

import java.time.Instant

import cats.syntax.either._
import io.parsek.Decoder.Result
import io.parsek.PValue._
import io.parsek.{Decoder, PValue}

/**
  * @author Andrei Tupitcyn
  */
trait DecoderInstances {
  implicit val idDecoder: Decoder[PValue] = Decoder.partial[PValue] {
    case v: PValue => Right(v)
  }

  implicit val nullDecoder: Decoder[Unit] = Decoder.partial[Unit] {
    case Null => Right(())
  }

  implicit val booleanDecoder: Decoder[Boolean] = Decoder.partial[Boolean] {
    case PBoolean(v) => Right(v)
    case PString(str) => Either.catchNonFatal(str.toBoolean)
  }

  implicit val intDecoder: Decoder[Int] = Decoder.partial[Int] {
    case PInt(v) => Right(v)
    case PLong(v) => Right(v.toInt)
    case PDouble(v) => Right(v.toInt)
    case PString(v) => Either.catchNonFatal(v.toInt)
  }

  implicit val longDecoder: Decoder[Long] = Decoder.partial[Long] {
    case PInt(v) => Right(v.toLong)
    case PLong(v) => Right(v)
    case PDouble(v) => Right(v.toLong)
    case PString(v) => Either.catchNonFatal(v.toLong)
    case PTime(v) => Right(v.toEpochMilli)
  }

  implicit val doubleDecoder: Decoder[Double] = Decoder.partial[Double] {
    case PInt(v) => Right(v.toDouble)
    case PLong(v) => Right(v.toDouble)
    case PDouble(v) => Right(v)
    case PString(v) => Either.catchNonFatal(v.toDouble)
  }

  implicit val stringDecoder: Decoder[String] = Decoder.partial[String] {
    case PString(v) => Right(v)
    case PInt(v) => Right(v.toString)
    case PLong(v) => Right(v.toString)
    case PDouble(v) => Right(v.toString)
    case PBoolean(v) => Right(v.toString)
  }

  implicit val instantDecoder: Decoder[Instant] = Decoder.partial[Instant] {
    case PTime(v) => Right(v)
    case PLong(v) => Right(Instant.ofEpochMilli(v))
  }

  implicit val timestampDecoder: Decoder[java.sql.Timestamp] = Decoder.partial[java.sql.Timestamp] {
    case PTime(v) => Right(java.sql.Timestamp.from(v))
    case PLong(v) => Right(new java.sql.Timestamp(v))
  }

  implicit val vectorDecoder: Decoder[Vector[PValue]] = Decoder.partial[Vector[PValue]] {
    case PArray(v) => Right(v)
  }

  implicit val mapDecoder: Decoder[Map[Symbol, PValue]] = Decoder.partial[Map[Symbol, PValue]] {
    case PMap(v) => Right(v)
  }

  implicit val bytesDecoder: Decoder[Array[Byte]] = Decoder.partial[Array[Byte]] {
    case PBytes(v) => Right(v)
  }

  implicit def optDecoder[A : Decoder]: Decoder[Option[A]] = new Decoder[Option[A]] {
    override def apply(v: PValue): Result[Option[A]] = v match {
      case Null => Right(None)
      case _ => implicitly[Decoder[A]].apply(v).map(Some.apply)
    }
  }
}

object DecoderInstances extends DecoderInstances
