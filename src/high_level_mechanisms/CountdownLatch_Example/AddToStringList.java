package high_level_mechanisms.CountdownLatch_Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * there are 5 countDownLatch, use countdown() to release each of them, other threads use
 * await() to get notified after the latch count reach zero.
 */

public class AddToStringList {

    public static void main(String[] args) throws InterruptedException {
        // create 5 threads to run the Worker task - which need latch and list of string
        // list is accessed by all threads, needs to be synchronized
        List<String> theList = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(5);
        //System.out.println("latch has "+ countDownLatch.getCount() + " before all threads");
        Stream.generate(()-> new Thread(() -> {
            try {
                countDownLatch.await();
                theList.add(Thread.currentThread().getName() + " -->");
                System.out.println("in outThread generator");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"OutThread -> "))
                .limit(2)
                .collect(Collectors.toList())
                .forEach(Thread::start);
        // use Stream to generate 5 threads
        Stream.generate(()->new Thread(new Worker(theList,countDownLatch)))
                .limit(5)
                .collect(Collectors.toList())
                .forEach(Thread::start);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // System.out.println("latch has "+ countDownLatch.getCount() + " after all worker threads");

//
//        countDownLatch.await();
//        theList.add("After all threads");
        Thread.sleep(2000);
        synchronized (new Object()){
            Iterator iterator = theList.iterator();
            while (iterator.hasNext()){
                System.out.println(iterator.next() + " ---->");
            }
        }
    }
}

class Worker implements Runnable {

    // each thread access countdownLatch and a string list
    private List<String> stringList;
    private CountDownLatch countDownLatch;
    Worker (List<String> theList, CountDownLatch countDownLatch){
        stringList = theList;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is adding to the list");
        stringList.add(Thread.currentThread().getName() + ", ");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }
}