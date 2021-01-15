package high_level_mechanisms.Semaphore_Examples;

import java.util.concurrent.Semaphore;

/**
 * with the semaphore, increment is fully finished before decrement can get the permit
 * to do the decrementing
 */
public class Semaphore_ShareCount {

    public static void main(String[] args) {
        Semaphore sem = new Semaphore(1);
        Thread tA = new MyThread(sem, "A");
        Thread tB = new MyThread(sem, "B");
        //Thread tC = new MyThread(sem, "C");
        tA.start();
        tB.start();
       // tC.start();
        try {
            tA.join();
            tB.join();
           // tC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Now the count is "+ Shared.count);
    }


}

class Shared {
   static int count= 0;

}

// this class will be instantiated by two or more threads to meet different conditions
class MyThread extends Thread{
    Semaphore semaphore;
    String name;
    MyThread (Semaphore sem, String threadName){
        super(threadName);
        semaphore = sem;
        name = threadName;
    }

    @Override
    public void run() {
        if(Thread.currentThread().getName()== "A") {
            try {
                semaphore.acquire();
                System.out.println(this.getName()+ " acquired the permit");
                // do operation
                for (int i = 0; i < 5; i++) {
                    System.out.println(this.getName() + " doing increment counting " + Shared.count++ );
                    Thread.sleep(10); // let other threads to work, but semaphore is not released, so no threads can access count variable
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
                System.out.println(this.getName() + " released the permit");
            }
        }
        else { // for other threads that's not "A"
            try {
                semaphore.acquire();
                System.out.println(this.getName() + " acquired the permit");
                // do operations
                for (int i = 0; i < 5; i++) {
                    System.out.println(this.getName() + " doing decrement counting "+ Shared.count--);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
                System.out.println(this.getName() + " released the permit");
            }
        }
    }
}
