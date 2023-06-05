package com.example.shoppingcart.alg

import cats.data.NonEmptyList
import com.example.shoppingcart.domain.{Product, ShoppingCart}

import java.util.UUID

trait ShoppingCartsRepo[F[_]] {
  def create(cart: ShoppingCart): F[ShoppingCart]

  def find(id: UUID): F[Option[ShoppingCart]]

  def update(cart: ShoppingCart, products: NonEmptyList[Product]): F[ShoppingCart]

  def deleteShoppingCart(id: UUID): F[Option[Unit]]
}
