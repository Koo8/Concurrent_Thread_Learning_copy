package high_level_mechanisms.CompletableFuture;

import java.util.concurrent.*;

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
 * thenApply is analogous to map and thenCompose is analogous to flatMap. Both Optional and Stream use these two APIs.
 */

public class SimpleFutureRole {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ////// method 1:
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->"Hello");// supplier is the parameter, no arg return something
        ////// method 2:
        // Future<String> completableFuture = CompletableFuture.completedFuture("Hello");  // Returns a new CompletableFuture that's completed with the given value.
       ////// method 3:
       // Future<String> completableFuture =  writeStringAsync();
        try {
            String str = completableFuture.get();
            System.out.println(str + " is the Hello future");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        // continue the method 1
        // if we want to do computation -> thenApply (Function) that return a Future that holds the result of the computation
        CompletableFuture<String> contatString = completableFuture.thenApply(string -> string + " World");
        System.out.println("New concatenated String " + contatString.get());  // get() throws 2 exceptions
//
//        // if the computation needs a value input, but doesn't return a value of any type, use thenAccept(Consumer)
//        CompletableFuture<Void> consumerFuture =contatString.thenAccept(string -> System.out.println("The word is " + string) ); // thenAccept() return void
//
//        // if we neither need an input value, nor need an returned value, use thenRun(Runnable)
//        CompletableFuture<Void> runnableFuture = consumerFuture.thenRun(()-> System.out.println("This future has no input, no output"));

        // monadic design pattern used in the methods thenCompose(Flatmap)
        ///// NOTE: the difference between thenCompose() and thenApply() is analogous to the difference between map and flatMap.
        CompletableFuture<String> comFut = CompletableFuture.supplyAsync(()->"Hello")
                .thenCompose(s->CompletableFuture.supplyAsync(()-> s + " world"));
        System.out.println("ComFut is " + comFut.get());

        // when execute two independent Futures and do something with their results, use thenCombine()
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(()-> "First Future")
                .thenCombine(CompletableFuture.supplyAsync(()-> " Second Future "), (a,b)->a+b); // thenCombine() return a CompletableFuture
        System.out.println("CF is " + cf.get());
        //

        // when execute two independent Future and don't do anything with their results, use thenAcceptBoth()
        CompletableFuture.supplyAsync(()->"MyFirstSentence")
                .thenAcceptBoth(CompletableFuture.supplyAsync(()-> " MySecondSentence"),(a, b)-> System.out.println(a +" and " + b)) ;


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
