package FactoryThreadCreation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * use ThreadFactory to create 10 threads, collect thread name id into List<String>
 */

public class ThreadFactory_Ex {

    public static void main(String[] args) {
        // use ThreadFactory to create 10 threads
        MyThreadFactory factory = new MyThreadFactory("GanThreadFactory");
        MyTask task = new MyTask();
        // create 10 threads using factory
        for (int i = 0; i <10 ; i++) {
           factory.newThread(task).start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(factory.getStats());
    }

    private static class MyThreadFactory implements ThreadFactory {

        // Fields name: factory name; count: as part of each thread unique name; stats: for collecting factory info
        private String name;
        private int count;
        private List<String> stats;
        MyThreadFactory(String name) {
            this.name = name;
            count = 0;
            stats = new ArrayList<String>();  // a list of thread names
        }
        // create a new thread with an unique name, add the thread name to the list<String>
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name+"__" + count); // give each new thread a unique name;
            count++;
            stats.add(String.format("Thread id %s with its name %s is created on %s\n", t.getId(),t.getName(), new Date()));
            return t;
        }

        public String getStats(){
            StringBuilder sb = new StringBuilder();
            for(String s:stats) {
                sb.append(s + "\n");
            }
            return sb.toString();
        }
    }

    private static class MyTask implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
