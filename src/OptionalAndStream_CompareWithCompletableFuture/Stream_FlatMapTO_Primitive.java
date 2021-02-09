package OptionalAndStream_CompareWithCompletableFuture;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * We can wrap int[] or long[] with Stream,
 * then add each element of long/int into LongStream or IntStream
 */

public class Stream_FlatMapTO_Primitive {

    public static void main(String[] args) {
        int[] intArray = {1,2,3,4,5,6};
        Stream<int[]>  numStream = Stream.of(intArray);  // NOTE: wrap int[] with stream
        IntStream  intStream = numStream.flatMapToInt(thisIntArray->Arrays.stream(thisIntArray));// Arrays.stream() use the provided int[] to create an IntStream
        intStream.forEach(f-> System.out.println(f));

        long[] longArray = {1,2,3,4,5,6};
        Stream<long[]> numStream1 = Stream.of(longArray);
        LongStream  longStream = numStream1.flatMapToLong(thisLongArray -> Arrays.stream(thisLongArray));
        long sum = longStream.reduce(0, (a, b)-> a+ b);
        System.out.println(sum);
    }
}
