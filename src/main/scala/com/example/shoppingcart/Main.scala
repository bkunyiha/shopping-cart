package com.example.shoppingcart

import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import doobie._
import doobie.hikari._
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

object Main extends IOApp {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  def loadConfig: IO[Config] = {
    ConfigSource.default.loadF[IO, Config]()
  }

  def transactor(config: DatabaseConfig): Resource[IO, HikariTransactor[IO]] = for {
    ce <- ExecutionContexts.fixedThreadPool[IO](config.fixedSizePool)
    xa <- HikariTransactor.newHikariTransactor[IO](
      config.driver,
      config.url,
      config.userName,
      config.password,
      ce
    )
  } yield xa

  def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- loadConfig
      res <- transactor(config.database).use { implicit xa =>
        ShoppingcartServer.run[IO](config).as(ExitCode.Success)
      }
    } yield res

  }
}
