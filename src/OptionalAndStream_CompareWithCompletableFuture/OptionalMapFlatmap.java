package OptionalAndStream_CompareWithCompletableFuture;

import java.util.Optional;

/**
 * Compare map vs. flatMap in Optional operations.
 */

public class OptionalMapFlatmap {

    public static void main(String[] args) {
        Optional<String> str = Optional.of("test");
       // System.out.println(str.get());
      // if(str.isPresent()) System.out.println(str.isEmpty());
       Optional<String> upperString = str.map(string -> string.toUpperCase());  // require to function to be processed
        str.map(String::toLowerCase);  // replace the above lambda with method reference
        // NOTE: map() don't change the value of "str", but to create a new Optional "upperString"
        System.out.println(str.map(String::toUpperCase).get());
        System.out.println(upperString.get());
        System.out.println(str.get());
         // highlight: 
        // when map()'s function parameter return another Option other than String, e.g.
        // then the returned type after map() is Optional<Optional<String>>, this time, instead of using
        // map ->
        System.out.println("Compare map vs. flatMap ------ ");
        Optional<Optional<String>> mappedString =str.map(s -> Optional.of("TEST"));
        System.out.println(mappedString.get().get());   // pretty cumberSome!!!, use flatMap instead
        // flatMap doesn't wrap an optional around another optional, like map does, so that flatten the process
        Optional<String> flatMappedString = str.flatMap(s ->Optional.of("TEST"));
        System.out.println(flatMappedString.get());
    }
}
