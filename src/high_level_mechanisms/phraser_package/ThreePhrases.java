package high_level_mechanisms.phraser_package;

import java.util.concurrent.Phaser;

/**
 * Phaser is like CyclicBarrier when only one phase is existing.
 * Each thread needs to register with the same phaser before arrive();
 * Phaser can deregister threads at different phases dynamically.
 * MUST register() thread with the phaser
 *
 */
public class ThreePhrases {

    public static void main(String[] args) {
        MyPhaser phaser = new MyPhaser(1); // Any thread using this phaser will need to first register for it.
        // if use this, this main thread has been registered with this phaser
       // Phaser phaser = new Phaser(1);
        new Thread(new PhasedTask(phaser,"A"));
        new Thread(new PhasedTask(phaser, "B"));
        new Thread(new PhasedTask(phaser, "C"));
       // phaser.register();
        phaser.arriveAndAwaitAdvance(); // now all threads phase value jumped from 0 to 1
//        System.out.println("Main: phase #" + phaser.getPhase() + " is the phase ");
        System.out.println("Main First Phase: "+ phaser.getRegisteredParties() + " parties are registered ");
        phaser.arriveAndAwaitAdvance(); // now all phase 1 is ready
//        System.out.println("Main Again:  " + phaser.getPhase() + " is the phase ");
        System.out.println("Main Second Phase: "+ phaser.getRegisteredParties() + " parties are registered ");

        phaser.arriveAndDeregister();
        System.out.println(Thread.currentThread().getName() + ": phraser is terminated ? ==> "  +phaser.isTerminated());
    }
}

class PhasedTask implements Runnable{
    MyPhaser phaser;
    String name;
    PhasedTask (MyPhaser phaser,String name){
        this.phaser = phaser;
        // highlight: must register the phaser inside the constructor
        this.phaser.register();
        new Thread(this, name).start();
    }
    @Override
    public void run() {
        toOnePhase(1);
        if(Thread.currentThread().getName() == "B") {
            phaser.arriveAndDeregister();
            System.out.println(Thread.currentThread().getName() + " is deregistered");
            return;
        } else {
            toOnePhase(2);
            phaser.arriveAndDeregister();
        }
        // getPhase() return the current phase value, Upon termination, the phase number is negative,
        //     "- Integer.MIN_VALUE which is -2147483645".
        //System.out.println("NOW 3 Step: " +phaser.getPhase() + " phase is processing under "+ Thread.currentThread().getName());

        System.out.println(Thread.currentThread().getName() + ": Phraser is terminated ? ==> "  +phaser.isTerminated());
    }

    private void toOnePhase(int i) {
       // System.out.println(i + " Step -->" +phaser.getPhase() + " phase is processing under "+ Thread.currentThread().getName());
        phaser.arriveAndAwaitAdvance();  // one phase ++ which means one phase advanced
       // System.out.println("After move the following to below the arrivalAndwaitAdvance()");
        System.out.println(i + " Step --> phase " +phaser.getPhase() + " in thread "+ Thread.currentThread().getName());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyPhaser extends Phaser {
    MyPhaser(int num){
        super(num);
    }

    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        if(phase == 2){
            System.out.println(Thread.currentThread().getName() + " is the last thread for the onAdvance()");
            return true;
        }
        return false;
    }
}
