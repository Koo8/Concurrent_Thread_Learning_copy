package high_level_mechanisms.Exchanger_OneProducer_OneConsumer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Exchanger;

public class Constant_Exchange {

    public static void main(String[] args) throws InterruptedException {
        Exchanger<Deque> ex = new Exchanger();

        Thread producer = new Thread("Producer" ){

            @Override
            public void run() {
                 Deque producerStack = new ArrayDeque<>(); // any size, removable from front or end, faster than list or stack

                // producer thread insert elements into stack
                while(producerStack.isEmpty()){
                    producerStack.add(System.nanoTime()%1000);    // precise current time, not necessarily good for calculating elapsed time
                    //stack.add(System.currentTimeMillis()); // alternative , but this is good for calculating elapsed time,

                    // do exchange
                    try {
                        System.out.println(Thread.currentThread().getName() + " is ready to exchange "+ producerStack);
                        producerStack =  ex.exchange(producerStack);
                        System.out.println(Thread.currentThread().getName() + " got " + producerStack);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        producer.start();
        Thread.sleep(300);

        Thread consumer = new Thread("Consumer"){

            @Override
            public void run() {
                  Deque consumerStack = new ArrayDeque<>();

                  do{
                      try {
                          System.out.println(Thread.currentThread().getName() + " is ready to exchange "+ consumerStack);
                          consumerStack = ex.exchange(consumerStack);
                          System.out.println(Thread.currentThread().getName() + " got "+ consumerStack);
                          // make sure each time stack passed to producer is empty for producer to keep on produce new time.
                          consumerStack.remove(); // equivalent to removeFirst(), will throw exception if null
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  } while(consumerStack.isEmpty());
            }
        };

        consumer.start();
    }
}
