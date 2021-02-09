package lock.Lock_And_Condition_For_Producer_Consumer.FileMock_MyOwnVersion;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * To create a generic buffer that utilize reentrantLock and two conditions for
 * producers and consumers to write to and read from
 * A Switch (boolean) is used for producer to control when to turn off consumer's waiting status.
 * No specific file database is introduced in this class, it is generic class. Producer is the class to
 * decide from where (a file) to fetch and write to where(the buffer)
 */
public class TheBuffer {
    private ReentrantLock lock;
    private Condition producerToAct, consumerToAct;
    private int maxSize;
    private LinkedList<String> list;
    // the Switch for producer to control consumer action
    private boolean allThreadsOn;

    TheBuffer(int maxSize) {
        this.maxSize = maxSize;
        list = new LinkedList<>();
        lock = new ReentrantLock();
        producerToAct = lock.newCondition();
        consumerToAct = lock.newCondition();
        allThreadsOn = true;
    }

    public void fetchString(String newLine) {
        lock.lock();
        try {
            // use condition
            while (list.size() == maxSize) {
                producerToAct.await();
            }
            //list.add(newLine); -> add() may throw an exception if it is null
            list.offer(newLine);  // -> offer() will return false when null is the param
            System.out.println(">>TheBuffer has "+ list.size());
            // for inform consumers
            consumerToAct.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public String readFromList() {
        String newLine = null;  //highlight: this string will be the whole document that created by all threads together.
        lock.lock();
        try {
            // add condition
            while ((list.size() == 0) && allThreadsOn) { // NOTE: the allThreadOn is the switch for consumer
                consumerToAct.await();  // allThreadOn condition is needed to tell consumer thread that only producer tells you to wait for more that consumers are worth waiting, otherwise kill the thread
                System.out.println(Thread.currentThread().getName() + " is awaiting because stringstorage is 0 AND okToProducer is true");
            }
            if (list.size() > 0) {
                // it shows this newLine always adds previous line
                newLine = list.poll();   // retrieve and remove the first element from linkedList
                System.out.printf("%s: Line Readed: list size -> %d\n",
                        Thread.currentThread().getName(),
                        list.size());
                producerToAct.signalAll();   // if this is outside of the if block, it will go wrong
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return newLine;
    }

    // set the swtich
    public void setAllThreadsOn(boolean theSwitch) {
        allThreadsOn = theSwitch;
    }

    public boolean isAllThreadsOn() {
        return allThreadsOn;
    }

    public int getListSize() {
        return list.size();
    }
}
