package FileMockPackageFromCuncurrentCookbook;

/**
 * refer to Lock_interface_Better_Than_sync -> Lock_and_condition_for_producer -> FileMock_MyOwnVersion package
 *
 */
public class Main {
    public static void main(String[] args) {
        FileMock mock = new FileMock(100, 10);
        Buffer buffer = new Buffer(20);
        Producer producer = new Producer(mock, buffer);
        Thread producerThread = new Thread(producer,"Producer");
        //Consumer consumers[] = new Consumer[3];
        Consumer consumer = new Consumer(buffer);
        Thread consumersThreads[] = new Thread[3];
        for (int i=0; i<3; i++){
            consumersThreads[i] = new Thread(consumer);
        }
        producerThread.start();
        for (int i = 0; i< 3; i++){
            consumersThreads[i].start();
        }
    }
}
