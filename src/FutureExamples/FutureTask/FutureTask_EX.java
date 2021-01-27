package FutureExamples.FutureTask;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * NOTE: FutureTask is future and runnable, it calls callable, can be used as a runnable
 */

public class FutureTask_EX {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            MyCallable myCallable = new MyCallable();
            FutureTask futureTask =  new FutureTask(myCallable); // callable is the parameter for futureTask
            executor.submit(futureTask); // futureTask implements runnable;
        }
        executor.shutdown();
    }
}

class MyCallable implements Callable<Integer> {  // callable must return something, for this return type is integer
    Random rand = new Random();
    @Override
    public Integer call() throws Exception {
        int num = rand.nextInt(5);
        System.out.println(num + ": " + Thread.currentThread().getName());
        return num;
    }
}
