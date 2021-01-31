package OptionalAndStream_CompareWithCompletableFuture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculate a total purchase cost, after 3 orders of 3 items each.
 * use map or flatMap for stream
 * BigDecimal calculation for price
 */

public class OderAndLineItemSum {
    public static void main(String[] args) {
        // create a few orders
        Order order1 = new Order();
        order1.addLineItem(new LineItem(1, "apple", 8, new BigDecimal(0.54)));
        order1.addLineItem(new LineItem(2, "chicken", 1, new BigDecimal(3.99)));
        order1.addLineItem(new LineItem(3, "bean", 3, new BigDecimal(1.89)));

        Order order2 = new Order();
        order2.addLineItem(new LineItem(4, "juice", 2, new BigDecimal(2.75)));
        order2.addLineItem(new LineItem(5, "flower", 1, new BigDecimal(19.99)));
        order2.addLineItem(new LineItem(6, "milk", 2, new BigDecimal(4.99)));

        Order order3 = new Order();
        order3.addLineItem(new LineItem(7, "pear", 5, new BigDecimal(0.99)));
        order3.addLineItem(new LineItem(8, "strawberry", 1, new BigDecimal(3.99)));
        order3.addLineItem(new LineItem(9, "soup", 1, new BigDecimal(7.99)));

        // add orders into an array
        List<Order> orders = new ArrayList<>();
        orders.add(order3);
        orders.add(order2);
        orders.add(order1);

        // calculate total cost of all orders
        BigDecimal totalPurchaseCost = orders.stream().map(Order::getOrderCost).reduce(BigDecimal.ZERO, /*BigDecimal::add*/(o1, o2) -> o1.add(o2)); // can be replaced with method reference
        totalPurchaseCost = totalPurchaseCost.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Total cost is " + totalPurchaseCost);

        // calculate total cost with flatMap - when don't use orderTotalCost, but lineItemTotalCost
        BigDecimal totalCost = orders.stream().flatMap(order-> order.getLineItems().stream()/*this stream() shows flatmap can flat one layer of stream*/).map(l->l.getTotalItemCost()).reduce(new BigDecimal(0),BigDecimal::add);
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Total cost is also "+ totalCost);
    }

}

class Order {
    List<LineItem> lineItems = null;
    BigDecimal totalOrderCost = BigDecimal.ZERO;

    void addLineItem(LineItem item) {
        if (lineItems == null) {
            lineItems = new ArrayList<>();
        }
        lineItems.add(item);
    }

    BigDecimal getOrderCost() {
        for (int i = 0; i < lineItems.size(); i++) {
            BigDecimal itemCost = lineItems.get(i).getTotalItemCost();
            totalOrderCost = totalOrderCost.add(itemCost);// NOTE: bigDecimal add
        }
        return totalOrderCost;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }
}

class LineItem {
    //  new LineItem(LogId, ItemName, quantity, UnitPrice[new BigDecimal("1.20")], totalCostThisLine [new BigDecimal("1.20")]);
    int id, quantity;
    String itemName;
    BigDecimal unitPrice = BigDecimal.ZERO, totalItemCost = BigDecimal.ZERO;

    LineItem(int id, String itemName, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        // BigDecimal multiply
        this.totalItemCost = unitPrice.multiply(new BigDecimal(quantity));
    }

    public BigDecimal getTotalItemCost() {
        return totalItemCost;
    }

}
