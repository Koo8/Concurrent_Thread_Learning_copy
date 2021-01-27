package FutureExamples.ForkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**  NOTE: Futures have several shortcomings.
 * 1.they cannot be manually completed and they do not notify when they are completed.
 * 2.Futures cannot be chained and combined.
 * 3.There is no exception handling.
 * To address these shortcomings, Java 8 introduced CompletableFuture, see that package
 *   if we pass the number 4 to our calculator, we should get the result from the sum of 4² + 3² + 2² + 1² which is 30.
 *   Use ForkJoinPool to invoke the forkJoinTask, which implements Future interface (get(), isDone(), isCancel()).
 */

public class RecursiveTask_FactorialSquareSum extends RecursiveTask<Integer> {

    private Integer n;
    // constructor needs an integer input
    public RecursiveTask_FactorialSquareSum(Integer n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if(n<= 1) {
            System.out.println(Thread.currentThread().getName() + " ->  When " + n + " the result is" + n);
            return n;
        }
       // if (n == 8) this.cancel(true);   // cancel() is from Future
        int result = n * n + new RecursiveTask_FactorialSquareSum(n-1).fork().join(); // highlight:
        System.out.println(Thread.currentThread().getName() + " ->  When " + n + ": the result is " + result);

        return result;
    }

    public static void main(String[] args) {
        RecursiveTask_FactorialSquareSum task = new RecursiveTask_FactorialSquareSum(10);
        ForkJoinPool pool = new ForkJoinPool(5);
        pool.invoke(task);
        System.out.println(task.isDone() + " that the calculation is finished"); // this method is from Future.
    }
}
