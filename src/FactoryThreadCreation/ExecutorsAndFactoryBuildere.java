package FactoryThreadCreation;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutorsAndFactoryBuildere {

    public static void main(String[] args) {
        ThreadFactory builder = new MyThreadFactoryBuilder().setNamePrefix("GanThreadPool")
                .setDeamon(false)
                .setPriority(10)
                .build();
        // executorService using threadFactory to create 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3, builder);
        SleepTask task1 = new SleepTask(1000);
        SleepTask task2 = new SleepTask(1000);
        SleepTask task3 = new SleepTask(1000);

        executor.execute(task1);
        executor.execute(task2);
        executor.execute(task3);

        executor.shutdown();   // since while(true) can't be finished, this line is useless.

    }

    // inner class - builder class using chained method by returning to the class itself
    private static class MyThreadFactoryBuilder {
        // only fields, no contructor // NOTE: this class create set() that return an instance of this class itself
        private String namePrefix = null;
        private boolean isDaemon = false;
        private int priority = Thread.NORM_PRIORITY;

        public MyThreadFactoryBuilder setNamePrefix(String namePrefix) {
            if (namePrefix == null) {
                throw new NullPointerException();
            }
            this.namePrefix = namePrefix;

            return this;
        }

        public MyThreadFactoryBuilder setDeamon(boolean b) {
            this.isDaemon = isDaemon;
            return this;
        }

        public MyThreadFactoryBuilder setPriority(int priority) {
            if (priority < Thread.MIN_PRIORITY) {
                throw new IllegalArgumentException(String.format("Thread priority (%s) must be bigger than %s \n", priority, Thread.MIN_PRIORITY));
            }
            if (priority > Thread.MAX_PRIORITY) {
                throw new IllegalArgumentException(String.format("Thread priority (%s) must be smaller than %s \n", priority, Thread.MAX_PRIORITY));
            }
            this.priority = priority;
            return this;
        }

        public ThreadFactory build() {

            return build(this);
        }

        private ThreadFactory build(MyThreadFactoryBuilder builder) {
            final String theNamePrefix = builder.namePrefix;
            final Boolean theDeamon = builder.isDaemon; // NOTE: wrapper the isDaemon to Boolean, so that != null can be used later
            final Integer thePriority = builder.priority;  // // NOTE: wrapper the isDaemon to Integer, so that != null can be used later

            AtomicLong index = new AtomicLong(0);
            return new ThreadFactory() {
                // innerclass for this thread's exception handler
                class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        System.out.println(t.getName() + " is throwing exception "+ e.getMessage());
                    }
                }

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setUncaughtExceptionHandler(new MyExceptionHandler());
                    if (theNamePrefix != null) {
                        t.setName(namePrefix + "--" + index.getAndIncrement());
                    }
                    if (theDeamon != null) {
                        t.setDaemon(theDeamon);
                    }
                    if (thePriority != null) {
                        t.setPriority(thePriority);
                    }

                    return t;
                }
            };
        }
    }
    // inner class- the runnable
    private static class SleepTask implements Runnable{
        private long sleepTime;
        SleepTask (long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            while(true) {
                try{
                    System.out.println(Thread.currentThread().getName() + " has priority of  " + Thread.currentThread().getPriority());
                    Thread.sleep(sleepTime);
                    int num = 10/(int)new Random(2).nextDouble(); // create "/ by zero" exception that will be caught by uncaughtExceptionhandler of the thread

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
