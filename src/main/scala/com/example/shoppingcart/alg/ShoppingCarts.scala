package com.example.shoppingcart.alg

import cats.data.NonEmptyList
import com.example.shoppingcart.domain.DomainTypes.ProductId
import com.example.shoppingcart.domain.{Product, ShoppingCart}

import java.util.UUID

trait ShoppingCarts[F[_]] {

  def create(cart: ShoppingCart): F[ShoppingCart]

  def find(id: UUID): F[Option[ShoppingCart]]

  def update(id: UUID, products: NonEmptyList[Product]): F[Option[ShoppingCart]]

  def removeProductItem(id: UUID, productId: ProductId): F[Option[ShoppingCart]]

  def deleteShoppingCart(id: UUID): F[Option[Unit]]
}