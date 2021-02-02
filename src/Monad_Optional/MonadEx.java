package Monad_Optional;

import java.util.Optional;

/**
 * Monads give us the ability to use pure functions in our imperative programs,
 * while dealing with all side effects ( the code that may go wrong)
 * Optional.map and Optional.flatMap both use monadic design pattern to deal with
 * side effect and therefore to maintain the function composition in the program
 */

public class MonadEx {
    public static void main(String[] args) {
        int a = 5;
        int b = 2;        // try use 0 instead
        // not a pure functional programming
//        divideThenAddTen(a, b);
//        // use monad -> embellished type to avoid "side effects" of computation and still can achieve function composition
//        divideThenAddTenMonad(a, b);
      Optional<Integer> result = divideAndAddTenWithMonad(a, b);
      if(result.isPresent()) System.out.println(result.get());           // Compare map() and flatMap()
                                                                         // map(Function<? super T, ? extends U> mapper)
      else System.out.println("No result");                              // flatMap(Function<? super T, ? extends ooOptional<? extends U>>oo mapper
    }                                                                    // flatMap unwrap the first layer Optional

    static Optional<Integer> divideAndAddTenWithMonad(Integer a, Integer b) {
        return divide(a,b)
                //If a value is present, returns the result of applying the given
                //     * {@code Optional}-bearing mapping function to the value, otherwise returns
                //     * an empty {@code Optional}. -> this is the same as map(), however, flatMap won't double wrap an variable with Optional
                ///// NOTE: this is the monad concept -> to use embellished type (Optional) to avoid "side effect"
                .flatMap(divisionResult -> addTen(divisionResult))//; // the input for this parameter is one layer inner of divide() which is integer.
                .flatMap( w -> addTen(w));
    }

    public static Optional<Integer> divide(Integer a, Integer b) {
        return (b == 0) ? Optional.empty() : Optional.of(a/b);
    }
    // this method is used inside flatMap() which require one layer inner of divide(int, int), so the parameter for this method is integer
    public static Optional<Integer> addTen(Integer a) { // highlight: the parameter is not an Optional<Integer>,
        return Optional.of(a + 10);
    }

//    private static Optional<Integer> divideThenAddTenMonad(int a, int b) {
//       Optional<Integer> result =  addTenMonad(divideMonad(a,b));
//        System.out.println("Divide Monad_Optional "+ result.get());
//        return result;
//    }                                                              // todo: use flatMap to solve manad problem
//
//    private static Optional<Integer> addTenMonad(Optional<Integer> divideMonad) {
//        Optional<Integer> final = divideMonad.flatMap(b->)
//    }
//
//    private static Optional<Integer> divideMonad(int a, int b) {
//        Optional<Integer> an = (b==0)?Optional.empty():Optional.of(a/b);
//        System.out.println("After division, the answer is " + an.get());
//        return an;
//    }
//
//    private static void divideThenAddTen(int a, int b) {
//        // feed result of one function into another -> achieve function composition
//        addTen(divide(a,b));
//    }
//    // add ten - pure functional programming
//    private static Integer addTen(Integer answer) {
//        return answer + 10;
//    }
//    // divide - not a pure functional programming
//    private static Integer divide(int a, int b) {
//        Integer answer = 0;
//        if(b == 0) {
//            System.out.println("Something goes wrong, can't find out answer");
//            return 0;
//        }
//        answer = a/b;
//        System.out.println("The answer is " + answer);
//        return answer;
//    }

}
