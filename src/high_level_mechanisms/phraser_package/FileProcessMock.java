package high_level_mechanisms.phraser_package;

import java.util.concurrent.Phaser;

/**
 * Phase 1 -> 3 threads to read the file
 * Phase 2 -> 2 threads to process the result
 * NOTE: oooo if instantiate a thread inside a runnable constructor, sometime one thread
 * is processed so fast that it deRegistered after another thread even registered yet, this
 * cause the phaser is null and exception will be thrown.
 */

public class FileProcessMock {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1); // registered with the thread.
        Thread t1 = new Thread(new FileProcessor(phaser),"FA");
        Thread t2 = new Thread(new FileProcessor(phaser),"FB");
        Thread t3 = new Thread(new FileProcessor(phaser),"FC");

        t1.start();
        t2.start();
        t3.start();

        phaser.arriveAndAwaitAdvance(); // the first phase is reached
        System.out.println(phaser.getRegisteredParties() + " threads at phase 1");
        phaser.arriveAndDeregister();// finished 2nd phase
        System.out.println("NOW -> is the phase "+ phaser.getPhase());
        System.out.println(phaser.getRegisteredParties() + " threads at phase 2");
        phaser.arriveAndDeregister();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(phaser.isTerminated() + " for phaser is terminated");


    }

}

class FileProcessor implements Runnable{
    Phaser phaser;

    public FileProcessor(Phaser phaser) {
        this.phaser = phaser;
        // must register
        phaser.register();
        // start this thread  -highlight: this make sometime cause one thread finished before the other one is registered yet, so IllegalStateException will be thrown
       // new Thread(this).start();
    }

    @Override
    public void run() {
       // take 20 millisecond to process file
        try {
            Thread.sleep(20);
            phaser.arriveAndAwaitAdvance(); // waiting for phase 1 to be completed by other threads
            System.out.println("The No." + phaser.getPhase() + " phase in thread " + Thread.currentThread().getName() );

            //if(Thread.currentThread().getName().equals("FC") /*|| Thread.currentThread().getName().equals("FB") */){
                Thread.sleep(30);
                phaser.arriveAndAwaitAdvance();
                System.out.println(phaser.getPhase() + " is finished by " + Thread.currentThread().getName());

           // }
//            if(Thread.currentThread().getName().equals("FB") /*|| Thread.currentThread().getName().equals("FB")*/ ){
//                Thread.sleep(30);
//                phaser.arriveAndAwaitAdvance();
//                System.out.println(phaser.getPhase() + " is finished by " + Thread.currentThread().getName());
//
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("in finally block " + Thread.currentThread().getName() + " is deregistered");
            phaser.arriveAndDeregister(); //
        }


    }
}

class QuerryTask implements Runnable {
    Phaser phaser;
    String name;

    public QuerryTask(Phaser phaser, String name) {

    }

    @Override
    public void run() {

    }
}
