package FutureExamples.FutureReturnedFromExecutor;

import java.util.concurrent.*;

public class ReturnAnIntegerAfterLongComputing {
    static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       Future<Integer> square  = calculateSquare(8);
        // NOTE: since get() wait for results, therefore blocks the execution, use isDone() to check the condition
        while(!square.isDone()) {
            System.out.println("Calculating...");
            Thread.sleep(100);   //***  work on other tasks when wait for the result
        }
        System.out.println(square.get()); // get() method to retrieve result from Future object.
       executor.shutdown();
    }

    public static Future<Integer> calculateSquare(Integer num){
        return executor.submit(new Callable<Integer>() {   //return a Future representing pending completion of the task
            @Override
            public Integer call() throws Exception {
                Thread.sleep(500);
                 return num * num;
            }
        });
    }
}
