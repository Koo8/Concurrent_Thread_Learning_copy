package GetPrimeNumbers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * run 10 threads to individually calculate the prime number counts within 20,000.
 * 5 even number threads have high priority,
 * 5 odd number threads have low priority.
 * each thread has 6 states: new, runnable, blocked, waiting, timed-waiting and terminated
 * each thread has its own ID.
 * thread state and id can't be modified.
 * <p>
 * Conclusion: the higher priority thread has been finished earlier. Thread has different states.
 */

public class Prime_Main {

    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        Thread.State[] states = new Thread.State[10];
        // run 10 threads to do the calculation respectively
        try (FileWriter file = new FileWriter("log.txt");
             PrintWriter print = new PrintWriter(file);) {
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(new CalculatePrimeOccurrence());
                states[i] = threads[i].getState();
                if (i % 2 == 0) {
                    threads[i].setPriority(Thread.MAX_PRIORITY);
                } else {
                    threads[i].setPriority(Thread.MIN_PRIORITY);
                }

                print.printf("Status of thread %d is %s\n", i, states[i]);
                threads[i].start();
            }

            boolean finished = false;
            while (!finished) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != states[i]) {
                        print.printf("Thread %s with id %d old state is %s, new state is %s\n", threads[i].getName(), threads[i].getId(),states[i], threads[i].getState());
                        states[i] = threads[i].getState();
                    }
                }
                finished = true;
                for (int i = 0; i < 10; i++) {
                    finished = finished && threads[i].getState() == Thread.State.TERMINATED;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
