package FileMockPackageFromCuncurrentCookbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class FileMock {
    private static String[] content;
    private int index;
    public FileMock(int size, int length){
        content = new String[size];
        for (int i = 0; i< size; i++){
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++){
                String randomCharacter= String.format("%d and %d ; ",i, j);
                buffer.append(randomCharacter);
            }
            content[i] = buffer.toString();
          //  System.out.println(content[i]);
        }
        index=0;
    }
    // used by producer to keep on reading from the file till index is too big
    public boolean hasMoreLines(){
        return index <content.length;
    }
    // get current index line of string, then increment the index
    public String getLine(){
        if (this.hasMoreLines()) {
            System.out.println("MockFile has : " + (content.length-index) + " strings left.");
            return content[index++];
        }
        return null;
    }
    public static void readFile() {
        File file = new File("content.txt");
        try(FileWriter fileWriter = new FileWriter(file)){
            Iterator iterator = Arrays.asList(content).iterator();
            while(iterator.hasNext()) {
                fileWriter.write(iterator.next().toString()+ "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileMock fileMock = new FileMock(100,10);
        fileMock.readFile();
    }
}
