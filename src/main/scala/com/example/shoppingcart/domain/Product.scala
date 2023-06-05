package com.example.shoppingcart.domain

import cats.data.NonEmptyList
import cats.effect.Concurrent
import com.example.shoppingcart.domain.DomainTypes._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import doobie._

case class Product(id: ProductId, description: String)

object Product {

  implicit val productDecoder: Decoder[Product] = deriveDecoder[Product]

  implicit def productEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, Product] =
    jsonOf

  implicit def productListEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, NonEmptyList[Product]] =
    jsonOf

  implicit val productEncoder: Encoder[Product] = deriveEncoder[Product]

  implicit def productEntityEncoder[F[_]]: EntityEncoder[F, Product] =
    jsonEncoderOf

  // Doobie
  implicit val roductRead: Read[Product] =
    Read[(String, String)].map { case (id, description) =>
      Product(ProductId(id), description)
    }
  implicit val roductWrite: Write[Product] =
    Write[(String, String)].contramap { product =>
      (product.id.id, product.description)
    }
}
