package lock;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * use lock interface and reentrantLock class to synchronize critical sections
 * use unLock to release the lock
 * use fair-mode (false or true) to control fairness
 */

public class PrintToDocument {
    public static void main(String[] args) {

        System.out.println(" --------- Start test for \"fair = false\" \n");
        testPrintJob(false);

        System.out.println(" --------- Start test for \"fair = true\" \n");
        testPrintJob(true);
    }

    private static void testPrintJob(boolean b) {
        PrintProcedure printProcedure = new PrintProcedure(b);
        Thread[] threads = new Thread[10];
        for (int i = 0; i <threads.length ; i++) {
           threads[i] = new Thread(new PrintTask(printProcedure), ""+i);
           threads[i].start();
        }
        for (int i = 0; i <threads.length ; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // highlight:  Lock and reentrantLock example
    //  this object uses lock to gain synchronization -> only one thread can access this object at one time
    private static class PrintProcedure {
        private Lock lock;
        private boolean fairMode;
        public PrintProcedure(boolean fairMode) {
            this.fairMode = fairMode;
            lock = new ReentrantLock(fairMode);
        }
        private void toPrint(){
            lock.lock(); // secure the lock keep other threads out
            String threadName = Thread.currentThread().getName();
            try(FileWriter fileWriter = new FileWriter("print_" +threadName +".txt")){
                for (int i = 1; i <=100 ; i++) {
                  fileWriter.write(i + ", " );
                  if(i%10 ==0) fileWriter.write("\n");
                }
               // fileWriter.write("\nby " + Thread.currentThread().getName()+ "\n");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            // do it twice  - to see the difference of order among all threads to receive the reentrantLock. Fair will apply to those that have waited for longest thread.
            lock.lock(); // secure the lock keep other threads out
            try{
                long duration = (long) (Math.random() * 2000);
                System.out.printf("PrintQueue: printing sleeping .... takes %d seconds \n", duration/1000);
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
    // The runnable task
    private static class PrintTask implements Runnable {
        PrintProcedure printProcedure;
        public PrintTask(PrintProcedure printProcedure) {
            this.printProcedure = printProcedure;
        }

        @Override
        public void run() {
            // record thread name
            System.out.printf("PrintTask is running by thread %s\n", Thread.currentThread().getName());
           printProcedure.toPrint();
        }
    }
}
