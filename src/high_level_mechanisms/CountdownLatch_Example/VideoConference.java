package high_level_mechanisms.CountdownLatch_Example;

import javax.xml.stream.events.StartElement;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VideoConference {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        StartConference startConference = new StartConference(countDownLatch);
        Thread thread = new Thread(startConference);
        thread.start();
        Stream.generate(()-> new Thread(new Paticipant(startConference)))
                .limit(10)
                .collect(Collectors.toList())
                .forEach(Thread::start);
    }
}

class StartConference implements Runnable{
    CountDownLatch countDownLatch;
    
    StartConference (CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    // check in each arrival
    void arrive(){

        try {
            Thread.sleep((long) (Math.random()* 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " arrived");

        countDownLatch.countDown();
        System.out.println("Waiting for " + countDownLatch.getCount() + " more participants to come");
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();
            System.out.println("Now the conference can start.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Paticipant implements Runnable {
    StartConference startConference;
    Paticipant(StartConference startConference){
         this.startConference = startConference;
    }

    @Override
    public void run() {
            startConference.arrive();
    }
}
