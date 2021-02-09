package OptionalAndStream_CompareWithCompletableFuture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Stream is another Monad, stream.count() can count the number of elements in the stream.
 * Read File into stream of lines -> Files.lines()
 * Break up lines into words -> stream.flatMap
 * Count total words -> stream.count
 */
public class WordCountInFile {
    public static void main(String[] args) throws IOException {

        Path path = Path.of("sampleText");
        //Read all lines from a file as a Stream.
        // Unlike readAllLines(Path, Charset), this method does not read all lines into a List,
        // but instead populates lazily as the stream is consumed.
        Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8); // throw io exception
        // turn Stream<String> of lines into Stream<String> of words
        Stream<String> words = lines.flatMap(line -> Stream.of(line.split(" +")));  // regex + -> match one or more space
        long numOfWords = words.count(); // Returns the count of elements in this stream.
    }
}
