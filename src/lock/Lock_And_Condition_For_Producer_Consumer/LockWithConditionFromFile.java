package lock.Lock_And_Condition_For_Producer_Consumer;


import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock -> reentrantLock, reentrantReadWriteLock(bad), stampedLook(good with optimized read mode)
 * This program
 * 1. create a file like class, generate 100 strings with each has 10 chars in it.
 * 2. create a dataBuffer that can retrieve strings from the file to the buffer
 * and read the strings from the buffer - oo here a lock with two conditions are used
 * 3. create 1 producer thread to fetch strings from file and put it into the buffer,
 * create 3 consumer threads to read from the buffer and process the strings
 */
public class LockWithConditionFromFile {

    public static void main(String[] args) {

        MyFile myFile = new MyFile(100, 10);
      //  DataTheBuffer dataBuffer = new DataTheBuffer(myFile);
        // create 1 producer thread
       // Producer producer = new Producer(dataBuffer);
    }

    private static class MyFile {

        private String[] content;
        private int index;

        // fill up the string array to create a file like object,
        // the index is for tracking the content strings
        MyFile(int numOfStrings, int charLength) {
            content = new String[numOfStrings];
            // insert random chars into the content
            for (int i = 0; i < numOfStrings; i++) {
                StringBuilder sb = new StringBuilder(charLength); // sb capacity is defined, by default it is 16 chars
                for (int k = 0; k < charLength; k++) {
                    sb.append(new Random().nextInt((int) Math.random() * 255));
                }
                content[i] = sb.toString();
            }
            index = 0;
        }

        private boolean hasMoreString() {
            return index < content.length;
        }

        private String getThisLineOfString() {
            if (hasMoreString()) {
                System.out.println("MyFile: " + (content.length - index));
                return content[index++];
            }
            return null;
        }
    }

    private static class DataTheBuffer {
        Lock lock;
        Condition empty, full;
        LinkedList<String> stringList;
        boolean hasLinesInBuffer;
        int maxSize;

        DataTheBuffer(int maxSize) {
            this.maxSize = maxSize; // TODO???
            lock = new ReentrantLock();
            empty = lock.newCondition();
            full = lock.newCondition();
            stringList = new LinkedList<>();
            hasLinesInBuffer = true; // TODO???
        }

        // used by producer thread
        private void put(String aLineOfString) {
            /*STEP 1*/
            lock.lock();// highlight: lock() should be outside of try block
            /*STEP 2*/
            try {
                /*CHECK condition*/
                while (stringList.size() == maxSize) {
                  full.await();  // wait till there are room for putting more strings
                }
                boolean isAdded =stringList.offer(aLineOfString);
                System.out.println("DataTheBuffer - get(), Thread "+ Thread.currentThread().getName()+ " "+ aLineOfString + " is added to make the size of stringList "+ stringList.size());
                /*NOTIFY condiion change*/
                empty.signalAll();  // won't be empty anymore, wake up threads waiting for "empty" lock
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*STEP 3*/
            finally {
                lock.unlock();
            }
        }
        // used by consumer thread
        private void get(){
            lock.lock();
            try{
               // while(stringList.size() == 0 && hasLinesInBuffer)

            } finally {
                lock.unlock();
            }
        }
    }
}
