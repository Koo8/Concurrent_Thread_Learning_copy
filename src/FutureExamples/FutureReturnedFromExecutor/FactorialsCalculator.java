package FutureExamples.FutureReturnedFromExecutor;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This program use BigInteger because
 * BigInteger class is used for mathematical operation which involves very big integer calculations
 * that are outside the limit of all available primitive data types.
 */

public class FactorialsCalculator {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var executor = Executors.newFixedThreadPool(2);
        List<Map<Integer, Future<BigInteger>>> listOfPairs = new ArrayList<>();
        Random rand = new Random();
        // instead put all pairs into the map
       // Map<Integer, Future<BigInteger>> pair = new HashMap<>();

        // create 6 threads to do 6 calculations
        for (int i = 0; i <6 ; i++) {
            // for each thread, we need a ramdom integer to start a calculation, and start the thread
            int value = rand.nextInt(10)+ 10;
            //System.out.println("Value is " + value);
            //System.out.println(Thread.currentThread().getName() + " : has initial value of "+ value);
            Map<Integer, Future<BigInteger>> pair = new HashMap<>();
            TheCalculator calculateTask = new TheCalculator(value);
            //Future<BigInteger> task = executor.submit(calculateTask);
            pair.put(value,executor.submit(calculateTask));
            listOfPairs.add(pair);

            // NOTE: instead using list,  put all value pairs into the map, then if the "value" repeat accidentally, the old pair will
            // be replaced with new pair, which is not wanted.
        }

       // Thread.sleep(2000);

        for(Map<Integer, Future<BigInteger>> couple: listOfPairs) {

           Optional<Integer> value =  couple.keySet().stream().findFirst();
           if(!value.isPresent()) {
               System.out.println("Key is not presented.");
               return;
           }
           int key = value.get();
            System.out.println("Key is " + key);
           Future<BigInteger> future = couple.get(key); // NOTE: for the get(), the parameter is "key" not "value",
          // if(future.isDone()){
               BigInteger result = future.get();
               System.out.println("For " + key + " the result is "+ result);

          // }

        }
//        // NOTE: can't use Map instead of List, map only allow one unique key in the set.
//        for(Map.Entry<Integer,Future<BigInteger>> thePair : pair.entrySet()  )  {
//            System.out.println("For value " + thePair.getKey() + " the Result is "+ thePair.getValue().get());
//        }
        executor.shutdown();
    }
}

// this is the parameter (a callable) used by executor.submit to return a future<BigInteger>
// to calculate the factorial of the provided number
class TheCalculator implements Callable<BigInteger> {
    int value ;
    TheCalculator(int value){
        this.value = value;
    }

    @Override  // return the factorial of the value provided
    public BigInteger call() throws Exception {
        var result = BigInteger.valueOf(1);  // var from java 10 local variable inferrence
        if(value == 0) {
            return result = BigInteger.valueOf(1);
        }
        for (int i = 1; i <=value ; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        Thread.sleep(500);
       // System.out.println("For value of " +value + " Result is " + result+ " : " + Thread.currentThread().getName() );

        return result;  // return type is BigInteger
    }
}
