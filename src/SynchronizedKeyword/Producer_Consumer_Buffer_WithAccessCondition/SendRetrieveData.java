package SynchronizedKeyword.Producer_Consumer_Buffer_WithAccessCondition;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Producer-consumer program - There is a condition to restrict both producer and consumer for access
 * the commonly used data storage. The condition is guarded by synchronized method and while block.
 */

public class SendRetrieveData {

    public static void main(String[] args) {
        // There is a common storage for storing new Date
        Storage storage = new Storage();
        // create producer and consumer both access the same storage object
        Producer producer = new Producer(storage); // the storage set() is run by this producer
        Consumer consumer = new Consumer(storage); // the storage get() is run by this consumer
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(producer);
        executor.submit(consumer);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        storage.toPrintOut();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        storage.toPrintOut();
        executor.shutdown();
    }
    // the data buffer that producer and consumer both needs to access for storing to and retrieving data from
    private static class Storage {
        private final int maxSize = 10;  // the queue has a fixed capacity
        private Queue<Date> dataList;
        private StringBuilder sb = new StringBuilder();
        Storage(){
           dataList = new LinkedList<>();
        }

        /**** Instead of using synchronized keyword, we can use lock + condition to synchronize producer_consumer programs*/
        // this will be implemented by producer to store data to buffer
        private synchronized void set() {
            // but there is a condition, this is the reason to synchronize this method - if the list capacity reaches the maxSize, the thread implementing this needs to wait for more room to occur;
            while(dataList.size() == maxSize){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // store data to buffer
            dataList.offer(new Date());
            System.out.printf("SET: list size is %d\n", dataList.size());
            // wake up other threads that are waiting (put to sleeping till be woken up)
            notify();
        }
        // this will be implemented by consumer to retrieve data from buffer
        private synchronized void get(){
            // the thread working on this retrieving needs to wait till there indeed are some new Date to be retrieved
            while(dataList.size() == 0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // retrieve data from buffer
            String newDate = dataList.poll().toString();
            sb.append(newDate + "\n");
            System.out.printf("GET: list size is %d\n", dataList.size());
            notify();
        }

        private void toPrintOut(){
            String result = sb.toString();
            System.out.println("----- Show result:");
            System.out.println(result);
        }
    }


    private static class Producer implements Runnable {
        Storage storage;
        Producer(Storage storage){
            this.storage = storage;
        }
        @Override
        public void run() {
            for (int i = 0; i <100 ; i++) {
                storage.set();
            }

        }
    }

    private static class Consumer implements Runnable {
        Storage storage;
        Consumer(Storage storage){
            this.storage = storage;
        }
        @Override
        public void run() {
            for (int i = 0; i <100 ; i++) {
                storage.get();
            }

        }
    }
}
