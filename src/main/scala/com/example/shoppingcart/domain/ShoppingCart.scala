package com.example.shoppingcart.domain

import cats.data.NonEmptyList
import cats.effect.Concurrent
import cats.implicits._
import doobie._
import doobie.postgres.circe.jsonb.implicits.{pgDecoderGet, pgEncoderPut}
import io.circe.generic.semiauto._
import io.circe.jawn._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.postgresql.util.PGobject

import java.util.UUID

case class ShoppingCart(id: UUID, products: NonEmptyList[Product])

object ShoppingCart {

  implicit val shoppingCartDecoder: Decoder[ShoppingCart] = deriveDecoder[ShoppingCart]

  implicit def shoppingCartEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, ShoppingCart] =
    jsonOf

  implicit val shoppingCartEncoder: Encoder[ShoppingCart] = deriveEncoder[ShoppingCart]

  implicit def shoppingCartEntityEncoder[F[_]]: EntityEncoder[F, ShoppingCart] =
    jsonEncoderOf

  implicit def shoppingCartOptionEntityEncoder[F[_]]: EntityEncoder[F, Option[ShoppingCart]] =
    jsonEncoderOf

  // Doobie
  implicit val uuidGet: Get[UUID] = Get[String].map(UUID.fromString)
  implicit val uuidPut: Put[UUID] = Put[String].contramap(_.toString)

  implicit val uuidMeta: Meta[UUID] = Meta[String].imap[UUID](UUID.fromString)(_.toString)

  implicit val productDecoder: Decoder[NonEmptyList[Product]] = deriveDecoder[NonEmptyList[Product]]
  implicit val productEncoder: Encoder[NonEmptyList[Product]] = deriveEncoder[NonEmptyList[Product]]

  implicit val shoppingCartMeta: Meta[ShoppingCart] = new Meta(pgDecoderGet, pgEncoderPut)
  implicit val productMeta: Meta[NonEmptyList[Product]] = new Meta(pgDecoderGet, pgEncoderPut)

  implicit val jsonMeta: Meta[Json] =
    Meta.Advanced.other[PGobject]("json").timap[Json](
      a => parse(a.getValue).leftMap[Json](e => throw e).merge)(
      a => {
        val o = new PGobject
        o.setType("json")
        o.setValue(a.noSpaces)
        o
      }
    )

  implicit val shoppingCartRead: Read[ShoppingCart] =
    Read[(UUID, NonEmptyList[Product])].map { case (x, y) => ShoppingCart(x, y) }
}
