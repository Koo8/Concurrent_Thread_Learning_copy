package LOCK_interface_Better_Than_Sync;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock has readLock and writeLock,
 * Multiple threads can read from data buffer, only one thread can write to buffer
 * used for frequent read operations and very few write operation
 */

public class ReentrantReadWriteLock_Ex {

    public static void main(String[] args) {
        // create 10 reader threads and 2 writer threads TODO:
        CommonList<Integer> list = new CommonList<Integer>(3, 6, 222, 50, -21);
        // create 10 reader threads
        ReadList readList = new ReadList(list);
        Thread[] threads = new Thread[10];
        for (int i = 0; i <10 ; i++) {
            threads[i] = new Thread(readList);
            threads[i].start();
        }
        WriteList writeList = new WriteList(list);
        Thread[] wThreads = new Thread[2];
        for (int i = 0; i <2 ; i++) {
            wThreads[i] = new Thread(writeList);
            wThreads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i <2 ; i++) {
            try {
                wThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Total size " + list.getSize());


    }

    // *** inner class
    // This common list is accessed by reader threads and writer threads. It needs both locks to
    // guarantee that only one writer thread can write while no other threads are reading or writing
    // when no writer thread writing, many reading threads can read the same time.
    private static class CommonList<Integer> {
        // use the lock to guard this list, so that reader and writer can use the respective locks to access the list
        ReentrantReadWriteLock reLock;
        List<Integer> list = new ArrayList<>();
        @SafeVarargs  // it says "possible heap pollution from the parameterized vararg type
        CommonList(Integer...elements){
            reLock = new ReentrantReadWriteLock();
            //list = Arrays.asList(elements);  // NOTE: retrun a fixed sized array, so can't be used
            list.addAll(Arrays.asList(elements));  //NOTE:  add to the end the elements in the collection
        }

        private void addElement(Integer theElement) {
            // this is the format to use writeLock
            reLock.writeLock().lock();// secure the writeLock
            try {
                list.add(theElement);
            } finally {
                reLock.writeLock().unlock();
            }
        }

        private Integer getElement(int index) {
            reLock.readLock().lock();
            Integer result;
            try{
                return result = list.get(index); // NOTO: return here won't stop the finally block from been implemented
            } finally {
                reLock.readLock().unlock();
            }
            //return result;
        }

        private int getSize(){
            reLock.readLock().lock();
            int size;
            try{
                return size = list.size(); // NOTO: return here won't stop the finally block from been implemented
            } finally {
                reLock.readLock().unlock();
            }
            //return size;
        }


    }
    private static class ReadList implements Runnable {
        CommonList<Integer> list;
        public ReadList(CommonList<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            Random rand = new Random();
            Integer ele =list.getElement(rand.nextInt(list.getSize()));   // this line of code required two readlocks and twice of unlocking.
            System.out.println(Thread.currentThread().getName() + " : get the element of " + ele + " at " + new Date().getTime());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class WriteList implements Runnable {
        CommonList<Integer> list;
        WriteList(CommonList<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            int num =   new Random().nextInt(100);
            list.addElement(num);
            System.out.println(Thread.currentThread().getName() + " : add " + num + "at " + new Date().getTime() );  // From the date we can see the writer has its own time while readers have shared time 
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
