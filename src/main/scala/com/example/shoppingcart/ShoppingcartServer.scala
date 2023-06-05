package com.example.shoppingcart

import cats.effect.Async
import com.comcast.ip4s._
import com.example.shoppingcart.repository.ShoppingCartsRepoPostgressImpl
import com.example.shoppingcart.routes.ShoppingcartRoutes
import com.example.shoppingcart.service.ShoppingCartPrg
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.{Logger => CatsLogger}
import doobie._

object ShoppingcartServer {

  def run[F[_] : Async: CatsLogger : Transactor](config: Config): F[Nothing] = {
    val shoppingCartPostgresDb = new ShoppingCartsRepoPostgressImpl
    val shoppingCartPrg = new ShoppingCartPrg(shoppingCartPostgresDb)

    val httpApp = ShoppingcartRoutes.shoppingcartRoutes[F](shoppingCartPrg).orNotFound

    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    for {
      _ <-
        EmberServerBuilder.default[F]
          .withHost(Host.fromString(config.host).get)
          .withPort(Port.fromString(config.port).get)
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
