package lock.StampedLock_Package;

import java.util.concurrent.locks.StampedLock;

public class Reader implements Runnable {
    private Position position;
    private StampedLock lock;
    Reader(Position p, StampedLock theLock){
        position = p;
        lock = theLock;
    }
    @Override
    public void run() {
        for (int i = 0; i <50 ; i++) {
           long stamp = lock.readLock();
           try {
               System.out.printf("Reader acquired lock : %d -> (%d , %d)\n", stamp, position.getX(), position.getY());
               Thread.sleep(200);
           } catch (InterruptedException e) {
               e.printStackTrace();
           } finally {
               lock.unlockRead(stamp);
               System.out.printf("Reader released lock: %d \n", stamp);
           }
        }
    }
}
