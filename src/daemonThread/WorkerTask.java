package daemonThread;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class WorkerTask implements Runnable {

    // both workerTask and CleanerTask needs to access the same deque
    private Deque<Event> deque;
    public WorkerTask(Deque<Event> deque) {
        this.deque = deque;
    }

    // create 100 events, save them in the deque, each time sleep 1 second after
    // and content is created.
    @Override
    public void run() {
        for (int i = 0; i<5 ; i++) {
            // create an content
            Event event = new Event();
            event.setDate(new Date());
            // display which thread is creating this content
            event.setContent("\""+Thread.currentThread().getId() + " is the thread that created event id " + i+ "\"");
            // add to deque
            deque.addFirst(event);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
