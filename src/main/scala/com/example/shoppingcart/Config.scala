package com.example.shoppingcart

case class Config(host: String, port: String, database: DatabaseConfig)

case class DatabaseConfig(driver: String, url: String, userName: String, password: String, fixedSizePool: Int)
