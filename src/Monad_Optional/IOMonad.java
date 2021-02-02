package Monad_Optional;

/**
 * Monad is a box that can either store a value or store its absence and which allows us to
 * compose the computations on this value.
 * -- monad must hava a binding operation -> transfer a value in one monad into another monad
 * -- monad must have a return operation to get a monad from a value
 * -- monad make program referential-transparent, pure function
 *
 * What monads do we have in java?
 * Optional.of{"Hello);
 * Stream.of("Hello","World");
 * CompletableFuture.supplyAsync(()->"Hello" + "World").thenApply(s -> s.toUpperCase())
 */

public class IOMonad {
    //external variable
     int EVNotPureMethod = 0;

    public static void main(String[] args) {
        IOMonad ioMonad = new IOMonad();
        // the following two results prove the method is not pure, not referential transparent
        var result1 = ioMonad.getSumIncrementEVNonPure(3,4) + ioMonad.getSumIncrementEVNonPure(3,4);
        System.out.println(ioMonad.EVNotPureMethod + ": " + result1);    // 2 : 14
        // reset the external variable for better comparison
        ioMonad.EVNotPureMethod = 0;
        var value = ioMonad.getSumIncrementEVNonPure(3, 4);
        var result2 = value + value;
        System.out.println(ioMonad.EVNotPureMethod + " : " + result2);   // 1 : 14
    }
    // getSum and increment external variable in a non pure method
    int getSumIncrementEVNonPure (int a, int b){
        final var sum = a + b;
        EVNotPureMethod += 1;   // NOTE: this value is not pure. // instead use IO monad- we can
                                // wrap a method call with a potential side effect inside, thereby making this method call lazy. side effect will only be called on demand
        return sum;
    }




}
// // first - express our effect as the Single Abstract Method interface -
// its method will return us some value of the effect execution.
interface Effect<T> {
    T run();
}
