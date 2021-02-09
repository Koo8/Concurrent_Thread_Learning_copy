package Monad_Optional;

import java.util.Optional;

/**
 * more monad examples
 * https://medium.com/@afcastano/monads-for-java-developers-part-1-the-optional-monad-aa6e797b8a6e
 */

public class Optional_Monad_Concept {

    public static void main(String[] args) {
        
    }

    public Optional<Integer> optionalAdd(Optional<Integer> val1, Optional<Integer>val2) {
        return   // don't need to check if var1 and var2 are empty, flatMap has this taken care of -> this is what monad is good for
                val1.flatMap(first -> val2.flatMap(second-> Optional.of(first + second) )) ;
    }
}
