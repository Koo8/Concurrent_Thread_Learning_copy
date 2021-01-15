package StampedLock_Package;

import java.util.concurrent.locks.StampedLock;

public class Main_StampedLock {

    public static void main(String[] args) {
        Position position = new Position(1,1);
        StampedLock lock = new StampedLock();
        Thread wThread = new Thread(new Writer(position,lock));
        Thread rThread = new Thread(new Reader(position, lock));
        Thread oThread = new Thread(new OptimisticReader(position, lock));
        wThread.start();
        rThread.start();
        oThread.start();
        try {
            wThread.join();
            rThread.join();
            oThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
