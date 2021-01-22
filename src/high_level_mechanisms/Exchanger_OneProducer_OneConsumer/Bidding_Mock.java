package high_level_mechanisms.Exchanger_OneProducer_OneConsumer;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;

/**
 * There are 3 parties to offer a bid,
 * First the seller passing the item price to bidders, each bidder offer a price to Middleman
 * the middleman choose the highest price passing to the seller
 * Seller <-> Middleman <-> 3 Buyers   => Total 5 threads
 */

public class Bidding_Mock {      // TODO: not showing result

    public static void main(String[] args) {
        Exchanger<Double> sellerEx = new Exchanger<>();
        Exchanger<Double> buyerEx1 = new Exchanger<>();
        Exchanger<Double> buyerEx2 = new Exchanger<>();
        Exchanger<Double> buyerEx3 = new Exchanger<>();
        SellerRun sR = new SellerRun(sellerEx, 10.0);
        BuyerRun bR1 = new BuyerRun(buyerEx1, "Buyer-1");
        BuyerRun bR2 = new BuyerRun(buyerEx2, "Buyer-2");
        BuyerRun bR3 = new BuyerRun(buyerEx3, "Buyer-3");
        MiddleManRun mD = new MiddleManRun(sellerEx, buyerEx1, buyerEx2, buyerEx3);

//        CompletableFuture.allOf(CompletableFuture.runAsync(sR),
//                CompletableFuture.runAsync(bR1),
//                CompletableFuture.runAsync(bR2),
//                CompletableFuture.runAsync(bR3),
//                CompletableFuture.runAsync(mD)).join();
    }

    private static class SellerRun implements Runnable {
        Exchanger<Double> ex;
        Double initPrice;

        public SellerRun(Exchanger<Double> sellerEx, Double initialPrice) {
            ex = sellerEx;
            initPrice = initialPrice;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                ex.exchange(initPrice);
               Double finalPrice =  ex.exchange(0.0);
                System.out.println("Seller got " + finalPrice + " from all buyers." );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class BuyerRun implements Runnable {
        Exchanger<Double> buyerEx;
        Random rand = new Random();
        String name;

        public BuyerRun(Exchanger<Double> buyerEx, String name) {
            this.buyerEx = buyerEx;
            this.name = name;
            new Thread(this,this.name).start();
        }

        @Override
        public void run() {
           // System.out.println("in Buyer run " + Thread.currentThread().getName());
            try {
                double buyerPrice = buyerEx.exchange(0.0);
               // System.out.println("Buyer " + Thread.currentThread().getName() + " buyerPrice from middleMan is "+ buyerPrice);
                Double offeredPrice = rand.nextInt(4) * buyerPrice +5.0;
                System.out.println("Buyer " + Thread.currentThread().getName() + " offeredPrice is "+ offeredPrice);
                buyerEx.exchange(offeredPrice);
                System.out.println(Thread.currentThread().getName() + " offers -- " + offeredPrice);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MiddleManRun implements Runnable {
        Exchanger<Double> sellerEx, buyerEx1, buyerEx2, buyerEx3;
        Double[] prices = new Double[3];

        public MiddleManRun(Exchanger<Double> sellerEx, Exchanger<Double> buyerEx1, Exchanger<Double> buyerEx2, Exchanger<Double> buyerEx3) {
            this.sellerEx = sellerEx;
            this.buyerEx1 = buyerEx1;
            this.buyerEx2 = buyerEx2;
            this.buyerEx3 = buyerEx3;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                Double sellerPrice = sellerEx.exchange(0.0);
                System.out.println("In middle Man: the seller price " + sellerPrice);
                buyerEx1.exchange(sellerPrice);
                System.out.println("In middle Man: the seller price " + sellerPrice);
                buyerEx2.exchange(sellerPrice);
                System.out.println("In middle Man: the seller price " + sellerPrice);
                buyerEx3.exchange(sellerPrice);

                // receive bids from buyers, then process
                Double buyer1Price = buyerEx1.exchange(0.0);
                prices[0] = buyer1Price;
                Double buyer2Price = buyerEx2.exchange(0.0);
                prices[1] = buyer2Price;
                Double buyer3Price = buyerEx3.exchange(0.0);
                prices[2] = buyer3Price;
                Double finalPrice = (double) Integer.MIN_VALUE;
                for (Double price : prices) {
                    if (finalPrice < price) {
                        finalPrice = price;
                    }
                }

                // pass the highest price to seller
                if(finalPrice != null) {
                    sellerEx.exchange(finalPrice);
                    System.out.println("MiddleMan is passing " + finalPrice + " to the seller.");

                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
