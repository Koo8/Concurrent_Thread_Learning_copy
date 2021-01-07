package threadLocal;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal keeps the value of an attribute the same to each thread, (when instantiated outside of the run())
 * without it, an attribute will be accessed by all threads implementing the runnable
 * task and keep on updating the value among all threads.
 */

public class CompareSafeUnsafe {

    public static void main(String[] args) {
        UnsafeTask unsafeTask = new UnsafeTask();
        SafeTask safeTask = new SafeTask();
        for (int i = 0; i <5; i++) {
            Thread threadUnsafe = new Thread(unsafeTask);
            Thread threadSafe = new Thread(safeTask);
//            threadUnsafe.start();
            threadSafe.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class UnsafeTask implements Runnable {
        private Date startDate; //= new Date(); --> // all threads have the same startDate as the first thread, no changes throughout the program among multiple threads
        @Override
        public void run() {
            // shared data can't keep locally invariant(unchanged)
           startDate = new Date();  // each thread has different startTime, shared attribute, value in each local thread will change after be accessed by multiple threads
            System.out.printf("Un - Thread %s startTime is ORIGINALLY %s \n", Thread.currentThread().getId(),startDate);
            try {
                //TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Un - Thread %s startTime should be %s \n", Thread.currentThread().getId(),startDate);
        }
    }
     //NOTE: ThreadLocal instances are typically oo private oo static fields in classes that wish to associate state with a thread.
    private static class SafeTask implements Runnable{
        //highlight: must instantiate the threadlocal variables outside the run(), otherwise it is not locally kept.
        private static ThreadLocal<Date> startDate = ThreadLocal.withInitial(()->new Date())
//                = new ThreadLocal<Date>(){
//            @Override          // set its initialValue
//            protected Date initialValue() {
//                return new Date();
//            }
//        }
        ;
        @Override
        public void run() {
            // highlight: if instantiate Threadlocal inside run, each thread will have different startDate, like unsafeTask, and can't be kept unchanged, just like the unSafeTask did
          //  startDate = ThreadLocal.withInitial(()->new Date());
            // Or use anonymous method
//            startDate = new ThreadLocal<>(){
//                @Override          // set its initialValue
//                protected Date initialValue() {
//                    return new Date();
//                }
//            };

            System.out.printf("Thread %s startTime is ORIGINALLY %s \n", Thread.currentThread().getId(),startDate.get());  // NOTE: use .get()
            try {
                //TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread %s startTime should be %s \n", Thread.currentThread().getId(),startDate.get());
            
        }
    }
}
