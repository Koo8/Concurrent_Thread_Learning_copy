package InterruptPrimeNumberCounting;

public class InterruPrime_Main {

    public static void main(String[] args) {
        Thread countPrime = new CountPrimeNumber();
        countPrime.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countPrime.interrupt();
        System.out.println(countPrime.isInterrupted() + " --> isInterrupted");   // true when interrupt() is called, false immediately after the thread finished execution. stay "true" for a very short of time
        System.out.println(countPrime.getState() + " --> State");
        System.out.println(countPrime.isAlive() + " --> isAlive");


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(countPrime.getState() + " --> State after 1 second");
        System.out.println(countPrime.isAlive() + " --> isAlive after 1 second");
        System.out.println(countPrime.isInterrupted() + " --> isInterrupted after 1 second");
    }
}
