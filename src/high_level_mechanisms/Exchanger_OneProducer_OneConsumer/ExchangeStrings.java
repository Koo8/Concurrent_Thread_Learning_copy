package high_level_mechanisms.Exchanger_OneProducer_OneConsumer;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExchangeStrings {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger();

        new MakeString(exchanger);
        new UseString(exchanger);
    }
}

class MakeString implements Runnable {
    Exchanger<String> ex;

    MakeString(Exchanger<String> exchanger) {
        ex = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run() {
        StringBuilder str = new StringBuilder();
        char c = 'A';
        for (int i = 0; i < 3; i++) {  // NOTE: totol 3 times to exchange info, the other thread needs 3 times to do exchange too
            for (int j = 0; j < 5; j++) {
                str.append(c++);
            }
            if (i == 2) {
                try {
                    // only wait for 250 millisecond for exchange info
                    str = new StringBuilder(ex.exchange(str.toString(), 250, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    System.out.println("OOPS, timeout");
                }
                continue;
            }
            try {
                 ex.exchange(str.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

class UseString implements Runnable {
    Exchanger<String>ex;

    UseString(Exchanger<String> exchanger) {
        ex = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run()  {
        for (int i = 0; i < 3; i++) {
            try {
                if (i == 2) {
                    Thread.sleep(500); // this time is longer than the 3rd exchange waiting time
                    throw new TimeoutException(); // for being able to kill the thread in case this exception is thrown
                }
               String str = ex.exchange("");
               if(str != null) {
                   System.out.println(str);
               }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                System.out.println("User couldn't get the string");
            }

        }
    }
}
