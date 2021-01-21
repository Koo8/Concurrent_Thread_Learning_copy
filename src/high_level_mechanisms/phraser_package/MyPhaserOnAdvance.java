package high_level_mechanisms.phraser_package;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * onAdvance() -> Overridable method
 *             -> perform an action upon impending phase advance
 *             -> control termination. (return true manually on override, or parties number to 0);
 * onAdvance() executed each time phaser changes the phase
 * It returns a false if Phaser continues its execution or the value true
 * if Phaser has finished and has to enter the termination state
 * TO subclass Phaser and override this method if you have to execute some
 * actions when you advance from one phase to the next.
 * Default returned value is true when registeredParties is zero
 *
 * This program is to simulate an exam, where there will be some students (Thread)
 * who have to do three exercises. All the students have to
 * finish one exercise before they proceed with the next one.
 */

public class MyPhaserOnAdvance extends Phaser {

    @Override
        protected boolean onAdvance(int phase, int registeredParties) {
        System.out.println(this.getPhase() + " Phase => onAdvance");
            switch (phase) {
                case 0:
                    return studentsArrived();
                case 1:
                    return finishFirstExercise();
                case 2:
                    return finishSecondExercise();
                case 3:
                    return finishExam();   // if this return true, phaser is terminated, then if there is a phase 4, the registered parties will still be the same as the number that it holds before termination
                default:
                    return true;
            }
        }

        private boolean finishExam() {
            System.out.print(this.getPhase() + " phase --> Phaser: All the students have finished the exam.\n");
           // System.out.print("Phaser: Thank you for your time.\n");
            return false;
            // Highlight: without true from onAdvance() finally, the phaser wont' be terminated
           // return true;
        }

        private boolean finishSecondExercise() {
            System.out
                    .print(this.getPhase() + " phase --> Phaser: All the students have finished the second exercise.\n");
            //System.out.print("Phaser: It's time for the third one.\n");
            return false;
        }

        private boolean finishFirstExercise() {
            System.out.print(this.getPhase() + " phase --> Phaser: All the students have finished the first exercise.\n");
           // System.out.print("Phaser: It's time for the second one.\n");
            return false;
        }

        /**
         * It writes two log messages to the console and returns the false value to
         * indicate that the phaser continues with its execution.
         *
         */
        private boolean studentsArrived() {
            //System.out.print("Phaser: The exam are going to start. The students are ready.\n");
            System.out.printf(this.getPhase() + " phase --> Phaser: We have %d students. Let's get start the exam\n",
                    getRegisteredParties());
            return false;
        }

        public static void main(String[] args) {
            MyPhaserOnAdvance phaser = new MyPhaserOnAdvance();
            // create 5 students, each use a thread that registered with phaser
            Student[] students = new Student[5];
            for (int i = 0; i < students.length; i++) {
                students[i] = new Student(phaser);
                phaser.register();
            }

            Thread[] threads= new Thread[students.length];
            for (int i = 0; i < students.length; i++) {
                threads[i] = new Thread(students[i], "Student " + i);
                threads[i].start();
            }
            System.out.println("Main: registered parties is " + phaser.getRegisteredParties());

            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();    // wait for all threads to be done before printing out
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // if inside finally, each thread will printout this notice
                   // System.out.printf("Main: The phaser has finished: %s.\n",phaser.isTerminated());
                  //  phaser.arriveAndDeregister();
                }
            }

            System.out.printf("Main: The phaser has finished: %s. There are %d parties in the phaser now\n",phaser.isTerminated(), phaser.getRegisteredParties());


        }

}

/**
 * This class will simulate the students of the exam
 *
 * @author bird September 23, 2014 at 8:01:47 PM
 */
class Student implements Runnable {

    private Phaser phaser;
    String threadName = Thread.currentThread().getName();

    public Student (Phaser phaser) {
        super ();
        this.phaser = phaser;
    }

    @Override
    public void run () { 
        System.out.printf ("%s: Has arrived to do the exam.Time: %s, Phase: %s\n", Thread
                .currentThread (). getName (), new Date().getTime(), phaser.getPhase());
        phaser.arriveAndAwaitAdvance ();
        System.out.printf ("%s: Is going to do the first exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (), new Date ().getTime(), phaser.getPhase());
        doExercise1 ();
        System.out.printf ("%s: Has done the first exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (), new Date ().getTime(), phaser.getPhase());
        phaser.arriveAndAwaitAdvance ();

        System.out.printf ("%s: Is going to do the second exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (), new Date ().getTime(), phaser.getPhase());
        doExercise2 ();
        System.out.printf ("%s: Has done the second exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (),  new Date ().getTime(), phaser.getPhase());
        phaser.arriveAndAwaitAdvance ();

        System.out.printf ("%s: Is going to do the third exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (), new Date ().getTime(), phaser.getPhase());
        doExercise3 ();
        System.out.printf ("%s: Has done the third exercise. Time : %s, Phase: %s\n", Thread
                .currentThread (). getName (),  new Date ().getTime(), phaser.getPhase());
        phaser.arriveAndAwaitAdvance ();
        // highlight: this is the added phase, like phase 4, since onAdvance() only have case up to 3, so this one fall into "default" case.
        // If Phaser has been terminated earlier(in case 3 to return true e.g), then this phase won't reduce the registered parties.
        // It will still be 5. Registered parties can only be deRegistered when the phaser is not terminated which is in "active" state
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        phaser.arriveAndDeregister();
        // TODO: why after deregister with phaser, there are still 5 parties left????
        System.out.println(Thread.currentThread().getName() + " detected that "+ phaser.getRegisteredParties() + " are still registered ");
    }

    private void doExercise3 () {
        try {
            long duration = (long) (Math.random () * 5);
            TimeUnit.SECONDS.sleep (duration);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    private void doExercise2 () {
        try {
            long duration = (long) (Math.random () * 5);
            TimeUnit.SECONDS.sleep (duration);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    private void doExercise1 () {
        try {
            long duration = (long) (Math.random () * 5);
            TimeUnit.SECONDS.sleep (duration);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

}
