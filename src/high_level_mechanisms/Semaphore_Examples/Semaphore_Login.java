package high_level_mechanisms.Semaphore_Examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

/**
 * To use Semaphore to limit users that can log into the system
 * This proves that when all permits of a semaphore are taken, other users
 * have to wait for new permit to be released
 */
public class Semaphore_Login {
    private Semaphore semaphore;
    private int slots;

    Semaphore_Login (int slot_limit){
        slots = slot_limit;
        semaphore = new Semaphore(slots);
    }

   boolean tryLogin(){
        // tryAcquire() return true when permit is available, then reduce
        // the number of available permit by one, otherwise return false;
        return semaphore.tryAcquire();  // semaphore.acquire() will block till receive the permit
    }
    void logout(){
        //Releases a permit to semaphore, increasing the number of available permits by one
        semaphore.release();
    }
    int availableSlots(){
        return semaphore.availablePermits();
    }

    void testBlockingOrNot() {

       // Semaphore_Login login = new Semaphore_Login(slots);
        ExecutorService executor = Executors.newFixedThreadPool(slots);
        // NOTE: loop thru using IntStream
        IntStream.range(0,slots/*-1*/).forEach( slot ->executor.execute(this::tryLogin)); // method passed inside the run() in the runnable
        // equals to the following for loop
//        for (int i = 0; i <slots ; i++) {
//          executor.execute(this::tryLogin);
//        }

        executor.shutdown();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println(this.availableSlots() + " slots left"); // without the sleep delay, this main thread may be executed b4 all threads processed
        System.out.println("You can login one more user : " + this.tryLogin());

        // check available slots left


    }

    public static void main(String[] args) {
        int slots = 10;
        Semaphore_Login login = new Semaphore_Login( slots);
        login.testBlockingOrNot();
    }
}
