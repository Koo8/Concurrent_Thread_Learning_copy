package daemonThread;

import java.util.Date;
import java.util.Deque;

/**
 * This is a daemon thread , extends thread instead implements runnable, to use setDaemon() method.
 */

public class CleanerTask extends Thread {
    // this is the same deque that workerTask created
    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        // oooo In the constructor, mark this thread as a daemon
        //thread with the setDaemon() method:
        //NOTE: call the setDaemon() method BEFORE you call the start() method otherwise will throw IllegalthreadstateException
        setDaemon(true);     // highlight
    }

    // daemon thread has an infinite loop (while(true)), it keeps on create a new Date to compare
    // with an content date, and do the clean() job
    @Override
    public void run() {
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    // when the content is 10 seconds long, remove it from the deque.
    private void clean(Date date) {
        // if deque is empty, do nothing;
        if (deque.size() == 0) return;
        // if been removed, print out message
        boolean removed = false;
        long elapsed;

        do {
            // get the last content  getlast() works the same here
            Event event = deque.peekLast();// since the deque is addFirst(), the last content is the oldest.
          //  System.out.println("ClearTask: content -> " + event.getContent());
            elapsed = date.getTime() - event.getDate().getTime();
            if (elapsed > 2000) {
                System.out.println("CleanerTask: the content being removed is " + event.getContent());
                deque.removeLast();
                // notify been removed;
                removed = true;
            }
        } while (elapsed > 10000);

        if (removed) {
            System.out.println("CleanerTask: size of the deque " + deque.size() );
        }
    }
}
