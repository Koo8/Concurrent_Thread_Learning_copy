package high_level_mechanisms.Semaphore_3printer;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This program allows 3 threads to get 3 permits from semaphore to access 3 printers for printing
 * each printer needs a lock to guarantee the exclusive access during the printing job
 * Each printer needs to unlock the printer first, then release the semaphore permit for other threads to
 * use printer for more printing job.
 */

class PrinterQueue
{
    //This Semaphore will keep track of no. of printers used at any point of time.
    private final Semaphore semaphore;

    //While checking/acquiring a free printer out of three available printers, we will use this lock.
    private final Lock printerLock;

    //This array represents the pool of free printers.
    private boolean freePrinters[];

    public PrinterQueue()
    {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        // fill up the boolean array
        Arrays.fill(freePrinters, true); //Assigns "true" to each element the array of booleans.
        printerLock = new ReentrantLock();
    }

    public void printJob(Object document)
    {
        try
        {
            //Decrease the semaphore counter to mark a printer busy
            semaphore.acquire();
            //highlight: this following block of code can't synchronize the printer for exclusive job
//            for (int i = 0; i <3 ; i++) {
//                if(freePrinters[i]) { // this condition can be accessed by at most 3 threads that passed thru by 3 permits of semaphore, that's why this failed
//                    System.out.println("printer "+ i + " is available to print----");
//                    freePrinters[i] = false;
//                    System.out.println("printer " + i + " is locked and do printing for " + Thread.currentThread().getName() + " at " + new Date().getTime());
//                    Thread.sleep(1000);
//                    freePrinters[i]= true;
//                    System.out.println("printer "+ i + " is BACK AGAIN for available to print----");
//                }
//            }

            //NOTE: Two things to achieve -> 1. to find the printer available, 2. to lock up the printer
            int assignedPrinter = getPrinter();

            //Print the job
            Long duration = (long) (Math.random() * 10000);
            System.out.println(Thread.currentThread().getName()
                    + ": Printer " + assignedPrinter
                    + " : Printing a Job during " + (duration / 1000)
                    + " seconds :: Time - " + new Date()); // at the same time, 3 printers can acquire semaphore permits
            Thread.sleep(duration);

            //Printing is done; Free the printer to be used by other threads.
            // NOTE: still needs the lock to guarantee the synchronization;
            releasePrinter(assignedPrinter);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            System.out.printf("%s: finished printing \n", Thread
                    .currentThread().getName());

            //Increase the semaphore counter back
            semaphore.release();
        }
    }

    //Acquire a free printer for printing a job
    private int getPrinter()
    {
        int availablePrinterIndex = -1;
        try {
            //Get a lock here so that only one thread can go beyond this at a time
            printerLock.lock(); //many threads can use this lock() to gain their own locks
            System.out.println("Locked " + Thread.currentThread().getName());

            //the job here is to get an available printer
            for (int i = 0; i < freePrinters.length; i++)
            {
                //If free printer found then mark it busy
                if (freePrinters[i])
                {
                    System.out.println("Printer "+ i + "is working for thread " + Thread.currentThread().getName());
                    availablePrinterIndex = i;
                    freePrinters[i] = false; // to claim this printer is busy
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            //Allow other threads to check for free printer
            printerLock.unlock();
            System.out.println("UnLocked " + Thread.currentThread().getName());
        }
        return availablePrinterIndex;
    }

    //Release the printer
    private void releasePrinter(int i) {
        try{
            printerLock.lock();
            //Mark the printer free
            freePrinters[i] = true;
        } finally {
            printerLock.unlock();
        }


    }
}