package FutureExamples.FutureTask;

import java.util.concurrent.*;

/**
 * Study Future: which has RecursiveTask,, RecursiveAction and FutureTask subclasses.
 * RecursiveTask and recursiveAction is from ForkJoinTask that can be implemented by ForkJoinPool,
 * which is from AbstractExecutorServices, they both can steal threads.
 * while futureTask is descent from runnable.
 * All futures can be get(), isDone() and cancel().
 *
 * TODO: this program shows thread results in a subsequent order???
 */

// the Contract --> all subclass needs to do search() of their own
interface ArchiveSearcher {
    String search(String target);
}

public class App implements ArchiveSearcher {
    ExecutorService executor = Executors.newFixedThreadPool(2);

    private void showSearch(final String target) {
        // method 1 use futureTask as runnable() in executor.execute()
        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
               return search(target);
            }
        });
        executor.execute(future); // executor.execute() not result will be returned.
        // method 2 use Future as returning type for executor.submit()
//        Future<String>  future
//                = executor.submit(() -> search(target));  // executor.submit(Callable() task that return a value ) -> return a Future

        try {
            if (!future.isDone()) {
                System.out.println(Thread.currentThread().getName() + " is doing other stuff....");
                displayOtherThings(); // do other things while searching   
            }
            displayText(future.get()); // get() is to get the result from Future
            // future.cancel(true);
            // System.out.println(future.isCancelled()+ " for cancelled." + future.isDone() + " for isDone.");

        } catch (ExecutionException e) {
            cleanup();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cleanup() {
        System.out.println("Execution Exception is caught.");
    }

    private void displayText(String s) {
        System.out.println("The result is " + s);
    }

    private void displayOtherThings() {
        System.out.println("Doing other stuff....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String search(String target) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return target + " founded.";
    }

    public static void main(String[] args) {
        App app = new App();
        app.showSearch("file ends with .txt");
        app.showSearch("file ends with .jpg");
        app.executor.shutdown();
    }
}
