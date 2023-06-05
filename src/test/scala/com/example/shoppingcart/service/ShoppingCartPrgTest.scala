package com.example.shoppingcart.service

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.example.shoppingcart.alg.ShoppingCartsRepo
import com.example.shoppingcart.domain.DomainTypes._
import com.example.shoppingcart.domain.{Product, ShoppingCart}
import munit.CatsEffectAssertions.assertIO
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import java.util.UUID


class ShoppingCartPrgTest extends AnyFunSuite with MockFactory {

  val mockDB = mock[ShoppingCartsRepo[IO]]
  val shoppingCartPrg = new ShoppingCartPrg[IO](mockDB)

  test("find ShoppingCart By Id Should Return Shopping Cart") {
    val cartId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")

    val sc = ShoppingCart(cartId, NonEmptyList.one(product))
    (mockDB.find _).expects(cartId).returning(IO.pure(sc.some)).once()

    val cartIO: IO[ShoppingCart] = shoppingCartPrg.find(cartId).map(_.get)
    assertIO(cartIO, sc)
  }

  test("create ShoppingCart Should Create A Shopping Cart") {
    val cartId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")

    val sc = ShoppingCart(cartId, NonEmptyList.one(product))
    (mockDB.find _).expects(cartId).returning(IO.pure(Option.empty[ShoppingCart])).once()
    //(mockDB.create _).expects(sc).returning(IO.pure(sc)).once()

    val cartIO: IO[ShoppingCart] = shoppingCartPrg.create(sc)
    assertIO(cartIO, sc)
  }

}