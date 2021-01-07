package GetPrimeNumbers;

public class CalculatePrimeOccurrence implements Runnable{
    private int num = 20000;
    private int counter =0;

    @Override
    public void run() {
        System.out.printf("Thread %s is starting.\n",Thread.currentThread().getName() );
        for (int i = 0; i <num ; i++) {
           if(isPrime(i)) counter++;
        }
        System.out.printf("Thread %s ended. There are %d prime numbers\n", Thread.currentThread().getName(), counter);
    }

    private boolean isPrime(int number) {
        if (number <= 2) return true;
        // assume it is prime unless it return false;
        for (int i = 2; i <number ; i++) {
            if(number % i == 0) return false;
        }
        return true;
    }

    public int getCounter() {
        return counter;
    }
}
