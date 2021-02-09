package lock.StampedLock_Package;

import java.util.concurrent.locks.StampedLock;

public class OptimisticReader implements Runnable {

    private Position position;
    private StampedLock lock;
    OptimisticReader(Position p, StampedLock theLock){
        position = p;
        lock = theLock;
    }

    @Override
    public void run() {
        long stamp;
        for (int i = 0; i <100 ; i++) {
             // try to get the lock
            try{
                stamp = lock.tryOptimisticRead();  // return 0 if we are unable to use the lock and a value different from 0 if we can use it
                if(lock.validate(stamp)) { // always use validate to see if we can access the data
                    System.out.printf("OptimisticReader acquired lcok: %d - (%d, %d) \n",stamp, position.getX(), position.getY());
                } else{
                    System.out.printf("OptimisticReader: %d not available\n", stamp);
                }
                Thread.sleep(200);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
