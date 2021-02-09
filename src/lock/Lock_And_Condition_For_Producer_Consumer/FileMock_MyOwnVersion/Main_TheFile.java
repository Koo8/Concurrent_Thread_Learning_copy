package lock.Lock_And_Condition_For_Producer_Consumer.FileMock_MyOwnVersion;

/**
 * refer to FileMockPackageFromConcurrentCookbook package
 * highlight: why the sb shows many duplicated lines,
 * OOOO check result, the comment may be faulty somewhere
 */
public class Main_TheFile {

    public static void main(String[] args) {
        FileCreated file = new FileCreated(100, 10);
        TheBuffer buffer = new TheBuffer(20);
        Producer_TheFile producer = new Producer_TheFile(file,buffer);
        Thread producerThread = new Thread(producer);
        Thread[] consumerThreads = new Thread[3];
        Consumer_TheFile consumer = new Consumer_TheFile(buffer);
        for (int i = 0; i < 3; i++) {
            consumerThreads[i]= new Thread(consumer);
        }
        producerThread.start();
        for (int i = 0; i <3 ; i++) {
            consumerThreads[i].start();
        }


    }
}
