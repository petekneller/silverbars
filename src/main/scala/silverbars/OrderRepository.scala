package silverbars

case class CustomerOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction)

trait OrderRepository {

  def placeOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit

  def deleteOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit

  def fetchOrders(): Seq[CustomerOrder]

}

class InMemoryRepository extends OrderRepository {

  // NOTE: Despite using a storage mechanism that preserves order, the ordering of items in storage is NOT important.
  // Using a set would make this lack of ordering explicit, however a set implies uniqueness and this API does not
  //   reject duplicates. Perhaps an implementation of a 'bag' would be more appropriate.
  var storage: Seq[CustomerOrder] = Seq.empty

  def placeOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit = {
    storage = storage :+ CustomerOrder(userId, quantity, price, direction)
  }

  def deleteOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit = {
    storage = storage.filter(order => order != CustomerOrder(userId, quantity, price, direction))
  }

  def fetchOrders(): Seq[CustomerOrder] = storage

}
