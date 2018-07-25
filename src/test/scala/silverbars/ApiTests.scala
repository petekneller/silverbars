package silverbars

import org.scalatest.{ FlatSpec, Matchers }

// NOTE: I like to use some high-level tests as documentation for a library/module.
// These tests ideally should be written in business/API language (rather than technical) and use
// real dependencies instead of mocks. I'd leave mocking for lower-level unit tests. The trade-off
// is these tests can become slow running and hard to set up, so need to be added/managed carefully.

// NB This API is NOT thread safe. Adding/deleting orders in different threads could result in data loss.
// This can be prevented by synchronizing access to placeOrder & deleteOrder
class ApiTests extends FlatSpec with Matchers {

  val user1 = "user1"

  "Live Order Board" should "accept well-formed orders" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, BUY)

    val summary = orderBoard.summary(BUY)
    summary.size should be (1)
    summary should contain (SummaryOrder(3.5, 303, BUY))
  }

  it should "reject ill-formed orders where..." in {
    // NOTE: It would be worth identifying what 'well-formed' means and
    // how the library indicates ill-formed orders
    succeed
  }

  // TODO uniqueness/duplication of orders?

  it should "accept deletes for orders" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, BUY)
    orderBoard.placeOrder(user1, 3.7, 305, BUY)

    orderBoard.summary(BUY).size should be (2)

    orderBoard.deleteOrder(user1, 3.5, 303, BUY)
    val orders = orderBoard.summary(BUY)
    orders.size should be (1)
    orders should be (Seq(SummaryOrder(3.7, 305, BUY)))
  }

  // TODO How does delete indicate that an order did not exist?

  "summary" should "merge BUYS of the same price and SELLs of the same" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, BUY)
    orderBoard.placeOrder(user1, 1.2, 303, BUY)
    orderBoard.placeOrder(user1, 0.7, 305, BUY)

    val summary = orderBoard.summary(BUY)
    summary.size should be (2)
    summary should contain (SummaryOrder(4.7, 303, BUY))
  }

  // NOTE: Is the following true? The paper requirements seem to imply it.
  it should "separate BUY and SELL orders" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, BUY)
    orderBoard.placeOrder(user1, 1.2, 1000, SELL)
    orderBoard.placeOrder(user1, 0.7, 305, BUY)

    orderBoard.summary(BUY).size should be (2)
    orderBoard.summary(SELL).size should be (1)
  }

  it should "order BUYs descending by price" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, BUY)
    orderBoard.placeOrder(user1, 1.2, 1000, BUY)
    orderBoard.placeOrder(user1, 0.7, 305, BUY)

    val summary = orderBoard.summary(BUY)
    summary should be (Seq(
      SummaryOrder(1.2, 1000, BUY),
      SummaryOrder(0.7, 305, BUY),
      SummaryOrder(3.5, 303, BUY)
    ))
  }

  it should "order SELLs ascending by price" in {
    val orderBoard = new LiveOrderBoard(new InMemoryRepository());
    orderBoard.placeOrder(user1, 3.5, 303, SELL)
    orderBoard.placeOrder(user1, 1.2, 1000, SELL)
    orderBoard.placeOrder(user1, 0.7, 305, SELL)

    val summary = orderBoard.summary(SELL)
    summary should be (Seq(
      SummaryOrder(3.5, 303, SELL),
      SummaryOrder(0.7, 305, SELL),
      SummaryOrder(1.2, 1000, SELL)
    ))
  }

}
