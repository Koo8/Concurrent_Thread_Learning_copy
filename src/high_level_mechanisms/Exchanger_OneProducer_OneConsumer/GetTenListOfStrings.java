package high_level_mechanisms.Exchanger_OneProducer_OneConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

public class GetTenListOfStrings {
    public static void main(String[] args) {
        Exchanger<List<String>>  exchanger = new Exchanger<>();

        Thread producerTh = new Thread(new ProducerString(exchanger));
        Thread consumerTh = new Thread(new ConsumerString(exchanger));

        producerTh.start();
        consumerTh.start();
    }
}

class ProducerString implements Runnable {
    Exchanger<List<String>>  ex;
    List<String> buffer = new ArrayList<>();

    public ProducerString(Exchanger<List<String>> exchanger) {
        this.ex = exchanger;
    }

    @Override
    public void run() {
         // for 10 times to create a buffer with 10 strings
        for (int i = 0; i <10 ; i++) {
            for (int j = 0; j <10 ; j++) {
               buffer.add((i+1) + " Exchange: " + j );
            }
            try {
                buffer = ex.exchange(buffer); // send the list of string to the consumer, receive a buffer of empty strings
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ConsumerString implements Runnable {
    Exchanger<List<String>>  ex;
    List<String> buffer = new ArrayList<>();

    public ConsumerString(Exchanger<List<String>> exchanger) {
        this.ex = exchanger;
    }

    @Override
    public void run() {
        // receive 10 times of list of string
        for (int i = 0; i < 10; i++) {
            System.out.println(i + " list from producer: ");
            try {
                buffer = ex.exchange(buffer);
                // display the value and clear the buffer
                for (int j = 0; j <10 ; j++) {
                    System.out.println(buffer.get(0));
                    buffer.remove(0);  // keep on remove the first till the list is empty
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


