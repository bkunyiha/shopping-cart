package com.example.shoppingcart.routes

import cats.data.NonEmptyList
import cats.effect.Concurrent
import cats.implicits._
import com.example.shoppingcart.domain.DomainTypes.ProductId
import com.example.shoppingcart.domain.DomainTypes.UUIDCodec._
import com.example.shoppingcart.domain.{CartNotFound, DuplicateCart, Product, ShoppingCart}
import com.example.shoppingcart.service.ShoppingCartPrg
import org.http4s.dsl.Http4sDsl
import org.http4s.{DecodeFailure, HttpRoutes, Response}
import org.typelevel.log4cats.Logger

import java.util.UUID

object ShoppingcartRoutes {

  def shoppingcartRoutes[F[_] : Concurrent : Logger](S: ShoppingCartPrg[F]): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    def httpErrorResponse(implicit logger: Logger[F]): Throwable => F[Response[F]] = {
      case DuplicateCart(cartId) => Conflict(cartId)
      case CartNotFound(cartId) => NotFound(cartId)
      case _: IllegalArgumentException => BadRequest("Invalid Id")
      case _: DecodeFailure => BadRequest("Invalid Payload")
      case error =>
        logger.error(s"Internal Server Error WHen Executing Route Request $error") *>
          InternalServerError("")
    }

    HttpRoutes.of[F] {
      // Get Cart by ID
      case GET -> Root / "shoppingCart" / UUIDVar(cartId) =>
        (for {
          cart <- S.find(cartId).flatMap {
            _.fold[F[ShoppingCart]](CartNotFound(cartId).raiseError[F, ShoppingCart])(_.pure[F])
          }
          resp <- Ok(cart)
        } yield resp).handleErrorWith(httpErrorResponse)
      // POST Cart
      case req@POST -> Root / "shoppingCart" =>
        (for {
          // Decode a Product request
          product <- req.as[Product]
          shoppingCart = ShoppingCart(id = UUID.randomUUID(), products = NonEmptyList.one(product))
          res: ShoppingCart <- S.create(shoppingCart)
          // Encode a hello response
          resp <- Ok(res)
        } yield resp).handleErrorWith(httpErrorResponse)
      // Add Item To Cart
      case req@PUT -> Root / "shoppingCart" / "add" / UUIDVar(cartId) =>
        (for {
          // Decode a Product request
          products <- req.as[Product]
          res <- S.findAndAdd(cartId, products)
          // Encode response
          resp <- Ok(res)
        } yield resp).handleErrorWith(httpErrorResponse)
      // Update Cart
      case req@PUT -> Root / "shoppingCart" / "update" / UUIDVar(cartId) =>
        (for {
          // Decode a Product request
          products <- req.as[NonEmptyList[Product]]
          res <- S.findAndUpdate(cartId, products)
          // Encode response
          resp <- Ok(res)
        } yield resp).handleErrorWith(httpErrorResponse)
      // Remove Item From Cart
      case _@DELETE -> Root / "shoppingCart" / UUIDVar(cartId) / "prod" / prodId  =>
        (for {
          res <- S.removeProductItem(cartId, ProductId(prodId))
          // Encode response
          resp <- Ok(res)
        } yield resp).handleErrorWith(httpErrorResponse)
      // Remove Cart
      case _@DELETE -> Root / "shoppingCart" / UUIDVar(cartId) =>
        (for {
          _ <- S.deleteShoppingCart(cartId)
          resp <- Ok()
        } yield resp).handleErrorWith(httpErrorResponse)
    }
  }
}