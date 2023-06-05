# Scala-shopping-cart

Simple Shopping Cart

## Technical stack
- cats and cats-effect: basic functional blocks as well as concurrency and functional effects
- circe: JSON serialization library
- doobie: a pure functional JDBC layer for Scala.
- http4s: functional HTTP server and client built on top of fs2
- log4cats: standard logging framework for Cats
- pureconfig: configuration parsing library

## Running And Testing Locally
### Setting up docker postgres DB
cd shoppingcart/docker
docker compose up
#### To manualy query the database
docker exec -it docker-db-1 bash
psql -U docker -d shoppingcart
#### Start Server
sbt run
#### HTTP query example
Create a shopping cart 
- POST to http://localhost:8080/shoppingCart
- JSON Payload example {"id": "1", "description": "Water"}

Add product to shopping cart
- PUT to http://localhost:8080/shoppingCart/add/{CARTID}
- JSON Payload example {"id": "1", "description": "Food"}

Update shoppingcart
- PUT to http://localhost:8080/shoppingCart/update/{CARTID}
- JSON Payload example [{"id": "1", "description": "Meat"}, {"id": "2", "description": "Potatos"}]

Delete Item From Cart
- DELETE http://localhost:8080/shoppingCart/{UUIDVar(cartId)}/prod/{ProdID}

Delete shopping cart
- DELETE http://localhost:8080/shoppingCart/{UUIDVar(cartId)}

### Run Unit Test
- Make sure docker is up and running for the repository test
- sbt test