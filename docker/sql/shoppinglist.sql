-- Database
CREATE DATABASE shoppingcart;
\c shoppingcart;

-- ShoppingCart
CREATE TABLE shopping_cart (
  _id SERIAL NOT NULL,
  id VARCHAR(36) NOT NULL,
  product JSON NOT NULL,
  PRIMARY KEY (id)
);
