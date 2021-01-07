package WaitForFinalization_Join;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskOne implements Runnable{

    @Override
    public void run() {
        System.out.println("TaskOne is starting..." + new Date());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("TaskOne is finished at " + new Date());
    }
}
