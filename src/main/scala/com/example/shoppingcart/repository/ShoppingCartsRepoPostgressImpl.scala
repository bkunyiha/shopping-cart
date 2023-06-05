package com.example.shoppingcart.repository

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits._
import com.example.shoppingcart.alg.ShoppingCartsRepo
import com.example.shoppingcart.domain.ShoppingCart._
import com.example.shoppingcart.domain.{Product, ShoppingCart}
import doobie._
import doobie.implicits._

import java.util.UUID

class ShoppingCartsRepoPostgressImpl[F[_] : Sync](implicit tx: Transactor[F]) extends ShoppingCartsRepo[F] {

  override def create(cart: ShoppingCart): F[ShoppingCart] = {
    sql"""INSERT INTO shopping_cart (id, product) VALUES (${cart.id}, ${cart.products})"""
      .update
      .withUniqueGeneratedKeys("id", "product").transact(tx)
  }

  override def find(id: UUID): F[Option[ShoppingCart]] = {
    sql"SELECT id, product FROM shopping_cart WHERE id = ${id.toString}"
      .query[ShoppingCart]
      .option
      .transact(tx)
  }

  override def update(cart: ShoppingCart, products: NonEmptyList[Product]): F[ShoppingCart] = {
    sql"""UPDATE shopping_cart SET product = $products WHERE id = ${cart.id}"""
      .update.withUniqueGeneratedKeys("id", "product").transact(tx)
  }

  override def deleteShoppingCart(id: UUID): F[Option[Unit]] = {
    sql"""DELETE FROM shopping_cart WHERE id = ${id}"""
      .update.run.transact(tx).map {
      case 0 => None
      case _ => Some(())
    }
  }
}
