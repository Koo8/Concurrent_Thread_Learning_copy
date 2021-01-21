package high_level_mechanisms.Exchanger_OneProducer_OneConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;

/**
 * Two threads exchange object to each other using the common exchanger and a buffer thread
 * 
 */
public class SimpleExchanger {

    public static void main(String[] args) {
        Exchanger<String> exchangerA = new Exchanger<>();
        Exchanger<String> exchangerB = new Exchanger<>();
        MyRunnable myRunnable1 = new MyRunnable(exchangerA, "Object-1");
        MyRunnable myRunnable2 = new MyRunnable(exchangerB,"object-2");
        BufferRunnable bRunnable = new BufferRunnable(exchangerA, exchangerB);
        Thread t1 = new Thread(myRunnable1, "t1");
        Thread t2 = new Thread(myRunnable2, "t2");
        Thread t3 = new Thread(bRunnable, "Buffer");
        CompletableFuture.allOf(CompletableFuture.runAsync(t1),CompletableFuture.runAsync(t2),CompletableFuture.runAsync(t3)).join();
    }
}
class MyRunnable implements Runnable {
    Exchanger<String> exchangerA;
    String string;

    public MyRunnable(Exchanger<String> exchangerA, String s) {
        this.exchangerA= exchangerA;
        this.string = s;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " has previous string " + string);
            String newStringA = exchangerA.exchange(string) ;
            String finalString = exchangerA.exchange(newStringA);
            System.out.println(Thread.currentThread().getName() + " has updated string " + finalString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class BufferRunnable implements Runnable {
    Exchanger<String> exchangerForA, exchangerForB;
    BufferRunnable(Exchanger<String> a, Exchanger<String> b) {
         this.exchangerForA =a;
         this.exchangerForB = b;
    }

    @Override
    public void run() {
        String strA = "strForA", strB = "strForB";
        try {
             String fromA = exchangerForA.exchange(strA);
             String fromB = exchangerForB.exchange(strB);
            System.out.printf("Buffer has %s from A and %s from B \n", fromA, fromB);
             String a = exchangerForA.exchange(fromB);
            String b = exchangerForB.exchange(fromA);
            System.out.printf("Buffer has %s from A and %s from B \n", a, b);




            // TODO get a lock


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
