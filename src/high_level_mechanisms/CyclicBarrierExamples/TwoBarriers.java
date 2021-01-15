package high_level_mechanisms.CyclicBarrierExamples;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**  This is the console result -- could be different
 * --2 waiting for b1
 * --1 waiting for b1
 * --1 is taking b1-Action   ==> thread 1 is the last to go thru b1, it is the thread to accomplish the action
 * The main main is here
 * --1 waiting for b2
 * --2 waiting for b2
 * --2 is taking b2 action   ==> thread 2 is the last to go thru b2, it is the thread to accomplish the action
 * Hooray! --2 crossed two barriers
 * Hooray! --1 crossed two barriers
 */

public class TwoBarriers {

    public static void main(String[] args) {
        
        // Threads need two cyclicBarriers, both barriers needs 2 barriers and its own actions
        Runnable b1Action = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " is taking b1-Action");
            }
        };
        Runnable b2Action = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " is taking b2 action");
            }
        };
        CyclicBarrier b1 = new CyclicBarrier(2, b1Action);
        CyclicBarrier b2 = new CyclicBarrier(2, b2Action);

        Thread t1 = new Thread(new TaskWithTwoBarriers(b1, b2), "--1");
        Thread t2 = new Thread(new TaskWithTwoBarriers(b1, b2), "--2");

        t1.start();
        t2.start();

        System.out.println("The main " + Thread.currentThread().getName() + " is here" );
    }
}

class TaskWithTwoBarriers implements Runnable{
    CyclicBarrier b1, b2;
    TaskWithTwoBarriers (CyclicBarrier b1, CyclicBarrier b2){
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public void run() {
        // await() for both barriers
        try {
            System.out.println(Thread.currentThread().getName() + " waiting for b1");
            b1.await();
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + " waiting for b2");
            b2.await();
            Thread.sleep(300);
            System.out.println("Hooray! " + Thread.currentThread().getName() + " crossed two barriers");

        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
