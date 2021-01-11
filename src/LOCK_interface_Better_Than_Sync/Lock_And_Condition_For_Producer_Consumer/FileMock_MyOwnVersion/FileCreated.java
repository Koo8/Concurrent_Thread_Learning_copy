package LOCK_interface_Better_Than_Sync.Lock_And_Condition_For_Producer_Consumer.FileMock_MyOwnVersion;

/**
 * To create a file for theBuffer class to read and fetch into the buffer - which
 * is the common place for producer and consumers to access
 */
public class FileCreated {
    private StringBuilder sb;
    private int index; // to go thru all lines of string for producer to fetch
    private String[] content;

    FileCreated(int lineOfStrings, int rowOfStrings ){
         sb = new StringBuilder();
         content = new String[lineOfStrings];
         // build the file
        for (int i = 0; i < lineOfStrings; i++) {
            for (int j = 0; j < rowOfStrings; j++) {
               sb.append(i +" of " + j + ", ");
            }
            sb.append("\n");
            content[i] = sb.toString(); // put strings into an array
        }
        index = 0;
    }

    String getLine(){
        if (hasPendingLines()){
            return content[index++];
        }
         return "The End";
    }

    public boolean hasPendingLines() {
        return index<content.length;
    }
}
