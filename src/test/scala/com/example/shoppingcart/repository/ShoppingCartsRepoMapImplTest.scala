package com.example.shoppingcart.repository

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.example.shoppingcart.domain.DomainTypes._
import com.example.shoppingcart.domain.{Product, ShoppingCart}
import munit.CatsEffectSuite

import java.util.UUID

class ShoppingCartsRepoMapImplTest extends CatsEffectSuite {

  val repo = new ShoppingCartsRepoMapImpl[IO](scala.collection.mutable.Map[UUID, ShoppingCart]())
  val cardId = UUID.randomUUID()
  val product = Product(ProductId("1"), "Corn")
  val sc = ShoppingCart(cardId, NonEmptyList.one(product))

  test("Should be able to save to repo") {
    assertIO(repo.create(sc), sc)
  }

  test("Should be able to query repo") {
    assertIO(repo.find(sc.id), sc.some)
  }

  test("Should be able to update repo") {
    val product = Product(ProductId("2"), "Beans")
    val sc = ShoppingCart(cardId, NonEmptyList.one(product))
    // update(cart: ShoppingCart, products: List[Product]): F[ShoppingCart]
    assertIO(repo.update(sc, NonEmptyList.one(product)), sc)
  }

}