package lock.StampedLock_Package;

import java.util.concurrent.locks.StampedLock;

public class Writer implements Runnable {
    private Position position;
    private StampedLock lock;
    Writer(Position p, StampedLock theLock){
        position = p;
        lock = theLock;
    }

    @Override
    public void run() {
        // for 10 times, we acquire the lock each time, change the position x and y.
        // sleep 1 second when write, sleep 1 more second after release the lock
        for (int i = 0; i < 10; i++) {
            long stamp = lock.writeLock();// lock acquired
            System.out.printf("Writer: Lock acquired with a stamp %d\n", stamp);
             try{
                position.setX( position.getX() +1);
                position.setY( position.getY() +1);
                Thread.sleep(100);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } finally {
                 lock.unlockWrite(stamp);  // always release the lock in the same block.
                 System.out.println("Writer: lock released " + stamp);
             }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
