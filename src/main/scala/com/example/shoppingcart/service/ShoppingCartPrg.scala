package com.example.shoppingcart.service

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits.toTraverseOps
import cats.syntax.all._
import com.example.shoppingcart.alg.{ShoppingCarts, ShoppingCartsRepo}
import com.example.shoppingcart.domain.DomainTypes.ProductId
import com.example.shoppingcart.domain.{CartNotFound, DuplicateCart, Product, ShoppingCart}

import java.util.UUID

class ShoppingCartPrg[F[_] : Sync](repo: ShoppingCartsRepo[F]) extends ShoppingCarts[F] {

  override def create(cart: ShoppingCart): F[ShoppingCart] =
    for {
      res <- find(cart.id).flatMap {
        _.fold[F[ShoppingCart]](repo.create(cart))(_ => DuplicateCart(cart.id).raiseError)
      }
    } yield res

  override def find(id: UUID): F[Option[ShoppingCart]] = repo.find(id)

  override def update(id: UUID, products: NonEmptyList[Product]): F[Option[ShoppingCart]] =
    for {
      maybeSc <- find(id)
      res <- maybeSc.traverse((sc: ShoppingCart) => repo.update(sc, sc.products ::: products))
    } yield res

  override def removeProductItem(id: UUID, productId: ProductId): F[Option[ShoppingCart]] = for {
    cart <- find(id).flatMap {
      _.fold[F[ShoppingCart]](CartNotFound(id).raiseError)(_.pure)
    }
    res <- cart.products.filterNot(_.id == productId).toNel match {
      case None => repo.deleteShoppingCart(cart.id).map(_ => None)
      case Some(prod) =>
        val updatedCart = ShoppingCart(cart.id, prod)
        repo.update(updatedCart, prod).map(Option(_))
    }
  } yield res

  override def deleteShoppingCart(id: UUID): F[Option[Unit]] = repo.deleteShoppingCart(id)

  def findAndUpdate(id: UUID, products: NonEmptyList[Product]): F[ShoppingCart] =
    for {
      sc <- find(id).flatMap(_.fold[F[ShoppingCart]](CartNotFound(id).raiseError)(_.pure))
      res <- repo.update(sc, products)
    } yield res

  def findAndAdd(cartId: UUID, product: Product): F[ShoppingCart] = {
    update(cartId, NonEmptyList.one(product)).flatMap {
      _.fold[F[ShoppingCart]](CartNotFound(cartId).raiseError)(_.pure)
    }
  }

}
