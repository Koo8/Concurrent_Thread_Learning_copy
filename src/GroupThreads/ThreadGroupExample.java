package GroupThreads;

import java.util.Random;

/**
 * ThreadGroup interrupt all the threads of the group with a single call.
 * for this program, to terminate all threads if one throws an exception
 */

public class ThreadGroupExample {

    public static void main(String[] args) {

        Random ran = new Random(12);
        System.out.println(ran.nextDouble()*1000000000);
        // instantiate task runnable and a threadGroup
        GanThreadGroup gtg = new GanThreadGroup("Gan Thread Group");
        MyTask task = new MyTask();

        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        for (int i = 0; i < numberOfThreads; i++) {
            // when creating threads, assign them to a threadGroup.
            Thread thread = new Thread(gtg, task);
            thread.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(gtg.activeCount() + " is the total threads in the threadGroup\n");
        gtg.list();  // to list all gtg information
        Thread[] threadArray = new Thread[gtg.activeCount()];
        int num = gtg.enumerate(threadArray);    //list all threads in the group into a thread array.
        for (int i = 0; i <num ; i++) {
            System.out.println(threadArray[i].getId() + " : " + threadArray[i].getState()+ "\n");
        }
    }

    // inner class
    private static class GanThreadGroup extends ThreadGroup {
        public GanThreadGroup(String name) {
            super(name);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t.getId() + " has thrown an exception.Reason -> " + e.getMessage());
           // e.printStackTrace();

            System.out.println("Terminating the rest of threads\n");
            interrupt();
        }
    }

    // provoke an arithmeticException exception by dividing 1,000 with random
    //numbers until the random generator generates zero to throw the exception:
    private static class MyTask implements Runnable {
        ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> {
            return 0;
        });
        @Override
        public void run() {
            int result;
            long randomBase = Thread.currentThread().getId();
           // System.out.println("MyTask: randomBase is " + randomBase + "\n");
            Random random = new Random(randomBase); // set seed
            while(true) {
                count.set(1);
                System.out.println(Thread.currentThread().getId() + " : run " + (count.get().intValue()+1) + " times \n"); // increment threadlocal integer value
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result = 1000 / (int) random.nextDouble();


                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getId() + " is interrupted. \n ");
                    return;
                }
            }
        }
    }

}
