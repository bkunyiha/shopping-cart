package com.example.shoppingcart.domain

import java.util.UUID

sealed trait ShoppingCartErrors extends Exception
case class DuplicateCart(error: UUID) extends ShoppingCartErrors
case class CartNotFound(error: UUID) extends ShoppingCartErrors
