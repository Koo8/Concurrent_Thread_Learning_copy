package LOCK_interface_Better_Than_Sync.Lock_And_Condition_For_Producer_Consumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition variables are instance of java.util.concurrent.locks.Condition class,
 * which provides inter-thread communication methods similar to wait, notify and notifyAll
 * e.g. await(), signal(), and signalAll().
 * So if one thread is waiting on a condition by calling condition.await()
 * the second thread can call condition.signal() or condition.signalAll() method
 * to notify that its time to wake-up, the condition has been changed.
 * <p>
 * Lock with condition is good for solving producer_consumer_programs. see how to
 * use synchronized keyword to solve producer consumer programs in
 * "producer_consumer_Buffer_withaccessCondition" package
 * <p>
 * Synchronized way for P_C programs is harder than lock+condition, but oo blockingQueue oo is
 * the easiest solution among the three options
 */

public class LockWithCondition {
    // create a commonBuffer that has a lock with two conditions and a queue, this buffer has get() for consumer and set() for producer
    // two locks shows capacity== full status and size==0 status
    public static void main(String[] args) {
        CommonBufferQueue bq = new CommonBufferQueue();
        Producer p = new Producer(bq);
        Consumer c = new Consumer(bq);
        Thread threadP = new Thread(p);
        Thread[] threadsC = new Thread[3];
        for (int i = 0; i <3 ; i++) {
             threadsC[i]=new Thread(c);
             threadsC[i].start();
        }
        threadP.start();
    }

    private static class CommonBufferQueue {
        private final int CAPACITY = 10;
        private Lock lock;
        private Condition full, empty;
        private Queue queue;
        private Random rand = new Random();

        CommonBufferQueue() {
            // this queue is protected by a lock with two conditions.
            lock = new ReentrantLock();
            full = lock.newCondition();
            empty = lock.newCondition();
            queue = new LinkedList();
        }
        // this method is used by producer thread
        private void putElement() {
            // get the lock
            /*Step 1*/
            lock.lock();
            /*Step 2*/
            try {
                // only when condition is met, otherwise wait
                while(queue.size()== CAPACITY) {
                    System.out.println("Queue is full, waiting..");
                    /*Condition 1*/
                     full.await();
                }
                int num = rand.nextInt();
                boolean isadded = queue.offer(num);
                if (isadded) {
                    System.out.println("PUT: " +Thread.currentThread().getName() + " : added " + num);

                    // inform other threads for new condition change
                    System.out.println("PUT: queue is not empty anymore, wake up those threads to get elements");
                    /*Condition 2*/
                    empty.signalAll();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*Step 3*/
            finally {
                lock.unlock();
            }
        }
         // this method is used by consumer threads
        private void getElement(){
            lock.lock();
            try{
                // only when queue is not empty
                while(queue.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + " is waiting because it is empty queue.");
                    empty.await();
                }

                // get Element
                int num = (int)queue.poll();
                System.out.println("GET: just retrieved "+ num );
                // notify threads the queue is not full anymore
                full.signalAll();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }

    private static class Producer implements Runnable{
        CommonBufferQueue theQueue;

        Producer(CommonBufferQueue queue){
            theQueue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                theQueue.putElement();
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static class Consumer implements Runnable {
        CommonBufferQueue theQueue;
        Consumer(CommonBufferQueue queue){
            theQueue = queue;
        }

        @Override
        public void run() {
            while(true) {
                theQueue.getElement();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            

        }
    }
}
