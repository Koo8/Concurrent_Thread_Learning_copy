package Monad_Optional;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Create a Try.class that handle exceptions and super two classes : Success and Failure
 * For two outcomes, two classes have different implementations
 *
 */

public class TryWithMonad {

    public static void main(String[] args) throws Throwable {
        // use Try.class as an anonymous class
//    Try<Integer> v = Try.ofThrowable(new Supplier<Integer>() {
//        @Override
//        public Integer get() {
//            return Integer.valueOf("1");
//        }
//    });
        // OR use lambda expression
        // supplier : takes no parameter, return a value
        Try<Integer> v = Try.ofThrowable(()->Integer.valueOf("1"));
        int vValue = Try.ofThrowable(()-> Integer.valueOf("twn"))
                .orElse(8);

        System.out.println(vValue);
        double dValue = Try.ofThrowable(()->Integer.valueOf("3"))
                .flatMap(s-> Try.ofThrowable(()->s/2.0))
                .get();     // add throwable
        System.out.println(dValue);

        int de = Try.ofThrowable(()-> 3)
                .flatMap(s -> Try.ofThrowable(()->s + 2))
                .get();
        System.out.println(de);
    }


}

// use abstract Try.class for monad, This class takes in whatever operation that might throw an exception
abstract class Try<U> {
    // this class has one method that handles exceptions that might be thrown
    public static <U> Try<U> ofThrowable(Supplier<U> f) {
        // do parameter validation check. if null, throw nullPointerException
        Objects.requireNonNull(f);
        try {
            return Try.successful(f.get()); // when no side effect, return its Success subclass
        } catch (Throwable e) {
            return Try.failure(e); // when there is a side effect, return its Failure subclass
        }
    }

    private static <U> Failure<U> failure(Throwable e) {
         return new Failure<>(e);
    }

    private static <U> Success<U> successful(U u) {
         return new Success<>(u);
    }
    
    public abstract U get() throws Throwable; // get the value inside the monad
    public abstract U orElse(U newValue);  // in case we can't get the value when fail, offer a new value
    //What we want to do is to pass in a computation/function
    // and apply it to the currently stored successful value.
    public abstract <T> Try<T> flatMap(Function<? super U, Try<T>> f);

    // subclasses
    static class Success<T> extends Try<T>{
        private final T value;
        Success(T u) {
            value = u;
        }

        @Override
        public T get() throws Throwable {
            return value;  // when successful, return the value
        }

        @Override
        public T orElse(T newValue) {
            return value;
        }

        @Override
        public <T1> Try<T1> flatMap(Function<? super T, Try<T1>> f) {
            Objects.requireNonNull(f);
            return f.apply(value);  // Function.apply(value)
        }
    }

    static class Failure<T> extends Try<T>{
        private final Throwable e;
        public Failure(Throwable e) {
            this.e = e;
        }

        @Override
        public T get() throws Throwable {
            System.out.println("oops");
            throw e; // when fail, throw the exception
        }

        @Override
        public T orElse(T newValue) {
            return newValue;
        }

        @Override
        //Since something that has failed can never become successful again we
        // basically just ignore the passed function and return
        // a new Failure.class containing the thrown exception from our original computation.
        public <T1> Try<T1> flatMap(Function<? super T, Try<T1>> f) {
            Objects.requireNonNull(f);
            return Try.failure(e);
        }
    }

}

