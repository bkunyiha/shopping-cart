package com.example.shoppingcart.repository

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.syntax.all._
import com.example.shoppingcart.alg.ShoppingCartsRepo
import com.example.shoppingcart.domain.{CartNotFound, Product, ShoppingCart}

import java.util.UUID
import scala.collection.mutable.Map

class ShoppingCartsRepoMapImpl[F[_] : Sync](db: Map[UUID, ShoppingCart]) extends ShoppingCartsRepo[F] {
  override def create(cart: ShoppingCart): F[ShoppingCart] = {
    db += (cart.id -> cart)
    val m = implicitly[Sync[F]]
    //cart.pure
    m.delay(cart)
  }

  override def find(id: UUID): F[Option[ShoppingCart]] = {
    db.get(id).pure
  }

  override def update(cart: ShoppingCart, products: NonEmptyList[Product]): F[ShoppingCart] = {
    find(cart.id).flatMap {
      _.fold[F[ShoppingCart]](CartNotFound(cart.id).raiseError) {
        (cart: ShoppingCart) =>
          db += (cart.id -> cart.copy(products = products))
          ShoppingCart(cart.id, products).pure
      }
    }
  }

  override def deleteShoppingCart(id: UUID): F[Option[Unit]] = {
    find(id).flatMap {
      _.fold[F[Option[Unit]]](CartNotFound(id).raiseError)(_ => db.remove(id).void.pure)
    }
  }

}
