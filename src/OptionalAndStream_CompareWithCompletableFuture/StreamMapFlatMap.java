package OptionalAndStream_CompareWithCompletableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * stream.map() [[a, b], [c, d]] vs. flatMap() [a,b,c,d]
 */

public class StreamMapFlatMap {

    public static void main(String[] args) {
        List<String> list = Stream.of("a", "b") // return a stream;
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println(list);
        // instead of using two map() to go deep into then merge again and again through the stream
        // we can use flatmap() to remove extra layer of stream wrapper
        List<List<String>> listInList =
                Stream.of(Arrays.asList("a", "b"), Arrays.asList("c", "d"))
                        // highlight: check this line!!!  There are map(map()) to un-twine the tangle
                        .map(l -> l.stream().map(String::toUpperCase).collect(Collectors.toList()))
                        .collect(Collectors.toList());
        System.out.println(listInList);  //highlight: return [[A, B], [C, D]]

        List<String> listFlatted =
                Stream.of(Arrays.asList("a", "b"), Arrays.asList("c", "d"))
                        .flatMap(l->l.stream().map(String::toUpperCase))// for each inner layer list, do the mapper
                        .collect(Collectors.toList());
        System.out.println(listFlatted);   // highlight:  return [A, B, C, D]

        // Test the flatMap in collection
        FlatMapInCollection<String> test = new FlatMapInCollection<>();
        List<List<String>>  listA = Arrays.asList(Arrays.asList("one"),Arrays.asList("two", "three"), Arrays.asList("four, five"));
        List<String> myList = test.forceToFlatList(listA);
        System.out.println("-----------");
        System.out.println(myList);
        List<String> yourList = test.toUseFlatMap(listA);
        System.out.println(yourList);

        // another flatMap example
        String[][] array = new String[][]{{"a", "b"}, {"c", "d"}, {"e", "f"}};

        // Java 8
        String[] result = Stream.of(array)  // Stream<String[]>
                .flatMap(a->Stream.of(a))//(Stream::of)// Stream<String>
                .filter(a -> a!="a")   // to remove a from the stream
                .toArray(String[]::new);    // [a, b, c, d, e, f]

        for (String s : result) {
            System.out.print(s + " ");
        }
    }
}

class FlatMapInCollection<T> {

    public <T> List<T> forceToFlatList(
            List<List<T>> nestedList) {
        List<T> ls = new ArrayList<>();
        nestedList.forEach(ls::addAll);  //  return is [one, two, three, four, five]
        return ls;
    }

    public <T> List<T> toUseFlatMap (List<List<T>> nestedList) {
       return nestedList.stream().flatMap(Collection::stream).collect(Collectors.toList());
                                         // equals to "l->l.stream()" to turn the subList to stream
          // return is [one, two, three, four, five]
    }
}
