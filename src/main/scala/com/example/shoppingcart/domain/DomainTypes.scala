package com.example.shoppingcart.domain

import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}
import io.estatico.newtype.macros.newtype
import java.util.UUID
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import doobie._

object DomainTypes {
  @newtype case class ProductId(id: String)

  object ProductId {
    implicit val circeProductIdDecoder: Decoder[ProductId] = (c: HCursor) => {
      c.value.asString match {
        case Some(v) => Right(ProductId(v))
        case None => Left(DecodingFailure(s"Can't decode: ${c.value}", List()))
      }
    }
    implicit val circeProductIdEncoder: Encoder[ProductId] = Encoder.encodeString.contramap[ProductId](_.id)

    // Doobie Implecit
    implicit val productIdGet: Get[ProductId] = deriving
    implicit val productIdPut: Put[ProductId] = deriving

    // Bidirectional schema mapping for Nat, in terms of Int
    //implicit val natGet: Get[ProductId] = Get[String].map(ProductId(_))
    //implicit val natPut: Put[ProductId] = Put[String].contramap(_.id)

  }

  object UUIDCodec {
    implicit val circeUUIDDecoder: Decoder[UUID] = (c: HCursor) => {
      c.value.asString match {
        case Some(v) =>
          try Right(UUID.fromString(v))
          catch {
            case _: IllegalArgumentException =>
              Left(DecodingFailure("Couldn't decode a valid UUID", c.history))
          }
        case None => Left(DecodingFailure(s"Can't decode: ${c.value}", List()))
      }
    }
    implicit val circeUUIDEncoder: Encoder[UUID] = Encoder.encodeString.contramap[UUID](_.toString)

    implicit def shoppingCartEntityEncoder[F[_]]: EntityEncoder[F, UUID] =
      jsonEncoderOf
  }


}
