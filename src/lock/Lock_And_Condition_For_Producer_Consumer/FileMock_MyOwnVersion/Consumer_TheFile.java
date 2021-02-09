package lock.Lock_And_Condition_For_Producer_Consumer.FileMock_MyOwnVersion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * read from the buffer when there are data printed and not fetched
 * will be killed by producer's switch
 */
public class Consumer_TheFile implements Runnable {
    TheBuffer buffer;

  //  StringBuilder sb;   // NOTE: stringbuilder is not needed. This "newRead" in run() is like a sb for all threads. sout return shows this

    Consumer_TheFile (TheBuffer theBuffer){
        buffer = theBuffer;
    }

    @Override
    public void run() {
       // System.out.println("@@@" + Thread.currentThread().getName() + " in run() ");
        // this string records all the content copied from all threads.
        // NOTE: this must be local variable, once it is out of the run() block, its content will be busted. so has to capture it within the run block
        String newRead = null;
        while(buffer.isAllThreadsOn() || buffer.getListSize()>0){
            newRead = buffer.readFromList();
           // sb.append(newRead);
            System.out.println("&&&&&&& " +  " : added new line -> \n^^^  "+ newRead + "\n");
        }
        System.out.println("########\n" + newRead); // all threads will return the same newRead content. this string is shared by all threads, because they all access this same consumer runnable task.
        writeToFile(newRead);
    }

    private void writeToFile(String newRead) {
        File file = new File( "test.txt");
        try(FileWriter fileWriter = new FileWriter(file)){
            if(newRead != null){
                fileWriter.write(newRead.toString());
            } else{
                fileWriter.write("No content");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
