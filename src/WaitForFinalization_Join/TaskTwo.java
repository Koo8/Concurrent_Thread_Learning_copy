package WaitForFinalization_Join;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskTwo implements Runnable{

    @Override
    public void run() {
        System.out.println("TaskTwo is starting..." + new Date());
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("TaskTwo is finished at " + new Date());
    }
}
