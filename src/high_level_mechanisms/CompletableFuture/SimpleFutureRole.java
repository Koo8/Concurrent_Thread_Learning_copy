package high_level_mechanisms.CompletableFuture;

import java.sql.Timestamp;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Future is for asynchronous computation
 * use completableFuture as a Future implementation, but with additional completion logic.
 * This program implements complete() and get(),
 * CompletableFuture static method supplyAsync(supplier), thenApply(function), thenAccept(consumer),
 * thenRun(runnable)
 *
 * NOTE: The best part of the CompletableFuture API is the ability
 * to combine CompletableFuture instances in a chain of computation steps. This chaining
 * feature is ubiquitous in functional programming, also referred to as monadic design pattern (optional
 * in java ia a monda example)
 *
 * thenApply is analogous to map and thenCompose is analogous to flatMap -> unwrap the layer down by one layer. Both Optional and Stream use these two APIs.
 */

public class SimpleFutureRole {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        ////// method 1:
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->"Hello");// supplier is the parameter, no arg return something
//        ////// method 2:
////         Future<String> completableFuture = CompletableFuture.completedFuture("Hello");  // Returns a new CompletableFuture that's completed with the given value.
//       ////// method 3:
//       // Future<String> completableFuture =  writeStringAsync();
//        try {
//            String str = completableFuture.get();
//            System.out.println(str + " is the Hello future");
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        // continue the method 1
//        // if we want to do computation -> thenApply (Function) that return a Future that holds the result of the computation
//        CompletableFuture<String> contatString = completableFuture.thenApply(string -> string + " World");
//        System.out.println("New concatenated String " + contatString.get());  // get() throws 2 exceptions
////
////        // if the computation needs a value input, but doesn't return a value of any type, use thenAccept(Consumer)
////        CompletableFuture<Void> consumerFuture =contatString.thenAccept(string -> System.out.println("The word is " + string) ); // thenAccept() return void
////
////        // if we neither need an input value, nor need an returned value, use thenRun(Runnable)
////        CompletableFuture<Void> runnableFuture = consumerFuture.thenRun(()-> System.out.println("This future has no input, no output"));
//
//        // monadic design pattern used in the methods thenCompose(Flatmap)
//        ///// NOTE: the difference between thenCompose() and thenApply() is analogous to the difference between map and flatMap.
//        CompletableFuture<String> comFut = CompletableFuture.supplyAsync(()->"Hello")
//                .thenCompose(s->CompletableFuture.supplyAsync(()-> s + " world"));
//        System.out.println("ComFut is " + comFut.get());
//
//        // when execute two independent Futures and do something with their results, use thenCombine()
//        CompletableFuture<String> cf = CompletableFuture.supplyAsync(()-> "First Future")
//                .thenCombine(CompletableFuture.supplyAsync(()-> " Second Future "), (a,b)->a+b); // thenCombine() return a CompletableFuture
//        System.out.println("CF is " + cf.get());
//        //
//
//        // when execute two independent Future and don't do anything with their results, use thenAcceptBoth()
//        CompletableFuture.supplyAsync(()->"MyFirstSentence")
//                .thenAcceptBoth(CompletableFuture.supplyAsync(()-> " MySecondSentence"),(a, b)-> System.out.println(a +" and " + b)) ;

       // After running a parallel of tasks, the ooCompletableFuture.allOf oo static method allows to wait for completion
        //  of all of the Futures provided as a var-arg:
       // testParallelTask();

        // Handle exception -> use handle() OR use completeExceptionally()
//        checkHandleException(null);
//        checkHandleException("Ning");

        // thenCompose() is when one future's result is the parameter of another future
        // thenCombine() is when two futures run independently and their results are needed for further computation
        calculateBMI();
    }

    private static void calculateBMI() throws ExecutionException, InterruptedException {
        // create a weightFuture
        CompletableFuture<Double> weightInKg = CompletableFuture.supplyAsync(()-> {
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 65.0;
        });
        // create a heightFuture
        CompletableFuture<Double> heightInCm = CompletableFuture.supplyAsync(()-> {
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 177.8;
        });

        CompletableFuture<Double> bmi = weightInKg.thenCombine(heightInCm,(a, b) -> a/(b*b/10000));// conver cm to m
        System.out.printf("BMI : %.2f ", bmi.get());    // NOTE: display two digit decimal
    }

    // using handle() to handle exceptions
    private static void checkHandleException(String name) throws ExecutionException, InterruptedException {
       // String name = null;
        CompletableFuture<String> f = CompletableFuture.supplyAsync(()->{
            if(name == null) {
               throw new RuntimeException("the message for exception can't be seen anywhere, as handle() has handled it.");
            }
            // supplyAsync() needs to return a <String>
            return "Hello " + name;
        }).handle((s,t) -> s!=null ? s : "Hello....??")  // s is the consumer, t is " Hello... ??"
        .thenApplyAsync(s -> s + " welcome.");
      //  System.out.println(f.get());  // get() will make the complete() call afterwards being ignored, because it is completed already for sure

        // to manually complete a future
        boolean confirm = f.complete("Completed.");  //
        System.out.println(confirm);
        System.out.println(f.get());  // the get() result is offered from complete();
       
    }

    private static void testParallelTask() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()-> "Hello");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(()->"Beautiful");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(()-> "World");
        // NOTE: return type is VOID not String
        CompletableFuture<Void> fAll = CompletableFuture.allOf(f1,f2,f3);
        fAll.join();
        System.out.println(fAll.get()); // throw exceptions    // this will return "null", because it is <Void> type
        // get the content from the f1,f2,f3, you can't use fAll, but wrap individual future inside stream
        String result = Stream.of(f1.get(), f2.get(),f3.get()).collect(Collectors.joining(" "));
        System.out.println(result);
    }

    private static Future<String> writeStringAsync() {
        CompletableFuture<String> task = new CompletableFuture<>();
        Future<String> taskOuter= Executors.newFixedThreadPool(1).submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                task.complete("Hello");   // the goal is to use this future as the return of this method
                return String.valueOf(3+2); // the goal is to test return null or anything <String> is fine. 
            }
        });
        // check what is for the taskOuter
        try {
            System.out.println(taskOuter.get() + " is the outer task");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return task;
    }
}
