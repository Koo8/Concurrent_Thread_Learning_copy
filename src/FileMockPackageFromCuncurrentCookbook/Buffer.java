package FileMockPackageFromCuncurrentCookbook;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {

    private final LinkedList<String> stringStorage;
    private final int maxSize;
    private final ReentrantLock lock;
    private final Condition consumerToGet;
    private final Condition full;
    // why? highlight => to keep all consumer threads waiting for the producer signals and finally be turn off by the producer
    private boolean okToProducer;

    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        stringStorage = new LinkedList<>();
        lock = new ReentrantLock();
        consumerToGet = lock.newCondition();
        full = lock.newCondition();
        okToProducer =true;
    }
    public void insert(String newString) {
        System.out.println("INSERT -> okToProducer is " + okToProducer);
        lock.lock();
        try {
            while (stringStorage.size() == maxSize) {
                full.await();
                System.out.println(Thread.currentThread().getName() + " waiting...");
            }
            stringStorage.offer(newString);  // add the string to the tail of the list
            System.out.printf("%s: Inserted Line: stringStorage size -> %d\n",
                    Thread.currentThread().getName(),
                    stringStorage.size());
            consumerToGet.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public String get() {
        String stringRetrievedByConsumer = null;
        lock.lock();
        try {
            while ((stringStorage.size() == 0) && /*hasPendingLines()*/ okToProducer ) {
                consumerToGet.await();  // okToProducer helps to terminate all the threads, no more waiting by consumers
                System.out.println(Thread.currentThread().getName() + " is awaiting because stringstorage is 0 AND okToProducer is true");
            }
            // since the switch of "okToProducer" is determined by producer thread,
            // if stringstorage.size == 0 the consumer can't poll from the buffer
            // if producer turn the switch of "okToProducer" off, the consumer don't need to wait forever.
            if (/*hasPendingLines()*/okToProducer || stringStorage.size()>0) {
                stringRetrievedByConsumer = stringStorage.poll(); // retrieve and remove the head of the list
                System.out.printf("%s: Line Readed: stringStorage size -> %d\n",
                        Thread.currentThread().getName(),
                        stringStorage.size());
                full.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return stringRetrievedByConsumer;
    }
    public synchronized void setOkToProducer(boolean okToProducer) {
        this.okToProducer = okToProducer;
    }
    public synchronized boolean hasPendingLines() {
        return okToProducer ||
                stringStorage.size()>0;
    }

    public synchronized boolean getOkToProducer(){
        return okToProducer;
    }
    public synchronized int getStringStorageSize(){
        return stringStorage.size();
    }




}
