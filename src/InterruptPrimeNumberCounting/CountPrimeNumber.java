package InterruptPrimeNumberCounting;

public class CountPrimeNumber extends Thread {
    private long number = 1L;

    @Override
    public void run() {
        while(true) {
            if(isPrime(number)) {
                System.out.printf("Prime number %d\n" , number);
            }
            if(isInterrupted()) {
                System.out.println("Task is interrupted.");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number) {
        if(number <= 2) return true;
        for (int i = 2; i <number ; i++) {
           if(number %i == 0) { // divisible
               return false;
           }
        }
        return true;
    }
}
