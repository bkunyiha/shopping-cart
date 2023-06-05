package com.example.shoppingcart.routes

import cats.data.NonEmptyList
import cats.effect.IO
import com.example.shoppingcart.domain.DomainTypes.{ProductId => ProdId}
import com.example.shoppingcart.domain.{Product, ShoppingCart}
import com.example.shoppingcart.repository.ShoppingCartsRepoMapImpl
import com.example.shoppingcart.service.ShoppingCartPrg
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.util.UUID

class ShoppingcartRoutesTest extends CatsEffectSuite {

  import ShoppingCardRoutesTest._

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  test("Get ShoppingCart By Id should Return 200") {
    assertIO(retGetCart.map(_.status), Status.Ok)
  }

  test("Get ShoppingCart By Id should Return Data") {
    assertIO(retGetCart.flatMap(_.as[ShoppingCart]), sc)
  }

  private[this] val retGetCart: IO[Response[IO]] = {
    val getCart = Request[IO](Method.GET, Uri.unsafeFromString(s"/shoppingCart/$cardId"))
    ShoppingcartRoutes.shoppingcartRoutes[IO](shoppingCartPrg).orNotFound(getCart)
  }
}

object ShoppingCardRoutesTest {
  val cardId = UUID.randomUUID()
  val product = Product(ProdId("1"), "Corn")
  val sc = ShoppingCart(cardId, NonEmptyList.one(product))
  val states = scala.collection.mutable.Map[UUID, ShoppingCart](cardId -> sc)
  val shoppingCartMapDb = new ShoppingCartsRepoMapImpl[IO](states)
  val shoppingCartPrg = new ShoppingCartPrg(shoppingCartMapDb)
}
