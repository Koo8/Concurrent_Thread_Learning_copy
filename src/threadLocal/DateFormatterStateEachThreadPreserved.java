package threadLocal;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * About inheritableThreadLocal https://zhangyuhui.blog/2018/01/12/leak-issue-of-inheritablethreadlocal-and-how-to-fix-it/
 * suggests not to casually use it for leaking prevention
 */

public class DateFormatterStateEachThreadPreserved implements Runnable{
    // define threadLocal as private static final is a convention to relate this to thread state
    private static final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.<SimpleDateFormat>withInitial(
            ()-> {return new SimpleDateFormat("yyyyMMdd HHmm");});
    @Override
    public void run() {
          // show formatter format
        System.out.printf("Thread %s has a format of %s\n", Thread.currentThread().getName(), formatter.get().toPattern());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // change format - that will only change the format of the local thread
        formatter.set(new SimpleDateFormat());
        System.out.printf("Thread %s has a format of %s\n", Thread.currentThread().getName(), formatter.get().toPattern());
    }

    public static void main(String[] args) {
        DateFormatterStateEachThreadPreserved task = new DateFormatterStateEachThreadPreserved();

        // create 5 threads to run the same task - that has its own threadLocal value
        for (int i = 0; i < 5 ; i++) {
             Thread thread = new Thread(task, ""+i);
             thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
