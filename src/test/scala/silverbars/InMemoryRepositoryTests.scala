package silverbars

import org.scalatest.{ FlatSpec, Matchers }

class InMemoryRepositoryTests extends FlatSpec with Matchers {

  val user1 = "user1"

  "an in-memory repository" should "store well-formed orders in a retrievable fashon" in {
    val repo = new InMemoryRepository()
    repo.placeOrder(user1, 1.2, 101, BUY)

    val orders = repo.fetchOrders()
    orders.size should be (1)
    orders(0) should be (CustomerOrder(user1, 1.2, 101, BUY))
  }

  // TODO: where does the responsibility of checking formed-ness of orders lie? In the order board, or the repo?

  it should "allow deleting of known orders" in {
    val repo = new InMemoryRepository()
    repo.placeOrder(user1, 1.0, 100, BUY)
    repo.placeOrder(user1, 1.2, 100, BUY)
    repo.placeOrder(user1, 1.0, 150, BUY)
    repo.placeOrder(user1, 1.0, 100, SELL)

    repo.deleteOrder(user1, 1.0, 150, BUY)
    val orders = repo.fetchOrders()
    orders.size should be (3)
    orders should not contain(CustomerOrder(user1, 1.0, 150, BUY))
  }

}
