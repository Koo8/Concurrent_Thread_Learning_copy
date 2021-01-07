package daemonThread;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Use daemon thread to clean up the deque like a garbage collector does
 */

public class Darmon_Main {

    public static void main(String[] args) {
        Deque<Event> eventQueue = new ConcurrentLinkedDeque<>(); // good to use when multiple threads access the same collection
        WorkerTask worker = new WorkerTask(eventQueue);
        // create the same number of threads as the processors
        // multiple threads add to the queue, the size will grow once to over 100 limits, 100 is the number for each thread to max create event number
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
             Thread thread = new Thread(worker);  // four threads creating
             thread.start();

        }
        // clearTask itself is the thread
        CleanerTask clearner = new CleanerTask(eventQueue);
        clearner.start();


    }
}
