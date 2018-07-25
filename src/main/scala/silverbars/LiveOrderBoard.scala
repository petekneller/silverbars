package silverbars

import scala.math.Ordering

// NOTE: The following of course assumes that order prices are whole numbers
case class SummaryOrder(quantity: BigDecimal, price: Int, direction: Direction)

class LiveOrderBoard(repo: OrderRepository) {

  // NOTE: we'd need to agree a convention for returning errors. In Java the following method would probably by marked:
  // throws IllegalArgumentException (or similar)
  // NOTE: userId should really be a richer type than String but we'd need to explore the structure/validation around this.
  def placeOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit =
    repo.placeOrder(userId, quantity, price, direction)

  def deleteOrder(userId: String, quantity: BigDecimal, price: Int, direction: Direction): Unit =
    repo.deleteOrder(userId, quantity, price, direction)

  def summary(direction: Direction): Seq[SummaryOrder] = {
    val orders = repo.fetchOrders().filter(order => order.direction == direction)
    orders.
      groupBy(order => order.price).
      mapValues(ordersInPriceBucket => sumOrderQuantities(ordersInPriceBucket)).
      map(pair => SummaryOrder(pair._2, pair._1, direction)).toSeq.
      sorted(if (direction == BUY) descending else ascending)
  }

  private def sumOrderQuantities(orders: Seq[CustomerOrder]): BigDecimal = orders.map(order => order.quantity).sum

  private val ascending: Ordering[SummaryOrder] = Ordering.fromLessThan((left, right) => left.price < right.price)

  private val descending: Ordering[SummaryOrder] = Ordering.fromLessThan((left, right) => left.price > right.price)

}
