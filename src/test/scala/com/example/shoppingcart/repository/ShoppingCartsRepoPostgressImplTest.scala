package com.example.shoppingcart.repository

import cats.data.NonEmptyList
import cats.effect._
import cats.effect.unsafe.implicits.global
import com.example.shoppingcart.domain.DomainTypes.ProductId
import com.example.shoppingcart.domain.{Product, ShoppingCart}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import doobie._
import doobie.implicits._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class ShoppingCartsRepoPostgressImplTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  private var postgres: EmbeddedPostgres = _
  private implicit var transactor: Transactor[IO] = _
  private var shoppingCartsRepoPostgresImpl: ShoppingCartsRepoPostgressImpl[IO] = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    postgres = EmbeddedPostgres.builder().start()
    transactor = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      postgres.getJdbcUrl("postgres"), // connect URL (driver-specific)
      "postgres", // user
      "postgres" // password
    )
    shoppingCartsRepoPostgresImpl = new ShoppingCartsRepoPostgressImpl[IO]

    sql"""CREATE TABLE shopping_cart (
         |  _id SERIAL NOT NULL,
         |  id VARCHAR(36) NOT NULL,
         |  product JSON NOT NULL,
         |  PRIMARY KEY (id)
         |)""".stripMargin
      .update
      .run
      .transact(transactor)
      .unsafeRunSync()
    ()
  }

  it should "Create a shoppingcart in" in {
    // given a shoppingcart sc
    val cardId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")
    val sc = ShoppingCart(cardId, NonEmptyList.one(product))

    // when
    val res = shoppingCartsRepoPostgresImpl.create(sc).unsafeRunSync()

    // then
    res.id should be(cardId)
  }

  it should "Create a query a shoppingcart in of one exists" in {
    // given a shoppingcart sc
    val cardId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")
    val sc = ShoppingCart(cardId, NonEmptyList.one(product))

    // when
    shoppingCartsRepoPostgresImpl.create(sc).unsafeRunSync()

    // then
    shoppingCartsRepoPostgresImpl.find(cardId).unsafeRunSync().get should be(sc)
  }

  it should "Update a shoppingcart in of one exists" in {
    // given a shoppingcart sc
    val cardId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")
    val sc = ShoppingCart(cardId, NonEmptyList.one(product))
    shoppingCartsRepoPostgresImpl.create(sc).unsafeRunSync()

    // when shoppingcart is updated
    val updatedProduct = Product(ProductId("1"), "Wheat")
    val updatedCart = ShoppingCart(cardId, NonEmptyList.one(updatedProduct))
    shoppingCartsRepoPostgresImpl.update(sc, NonEmptyList.one(updatedProduct)).unsafeRunSync()

    // then
    shoppingCartsRepoPostgresImpl.find(cardId).unsafeRunSync().get should be(updatedCart)
  }

  it should "Delete a shoppingcart in of one exists" in {
    // given a shoppingcart sc
    val cardId = UUID.randomUUID()
    val product = Product(ProductId("1"), "Corn")
    val sc = ShoppingCart(cardId, NonEmptyList.one(product))
    shoppingCartsRepoPostgresImpl.create(sc).unsafeRunSync()

    // when shoppingcart is updated
    shoppingCartsRepoPostgresImpl.deleteShoppingCart(cardId).unsafeRunSync()

    // then
    shoppingCartsRepoPostgresImpl.find(cardId).unsafeRunSync() should be(None)
  }

  override protected def afterAll(): Unit = {
    postgres.close()
    super.afterAll()
  }
}