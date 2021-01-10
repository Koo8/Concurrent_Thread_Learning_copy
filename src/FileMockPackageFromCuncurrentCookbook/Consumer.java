package FileMockPackageFromCuncurrentCookbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Consumer implements Runnable{
    private Buffer buffer;
    private String filePath;
    private File file;
    private StringBuilder sb;
    private int counter;
    public Consumer (Buffer buffer, String filePath) {
        this.buffer = buffer;
        // this filePath will only correspond to main thread if not "dynamic" part added in the run()
        this.filePath = filePath;
        sb = new StringBuilder();
        counter=0;
    }
    @Override
    public void run() {
        counter++;
        this.filePath += (Thread.currentThread().getName() + ".txt"); // create a dynamic file path that match each thread name
        System.out.println(Thread.currentThread().getName() +" ---> okToProducer is " +buffer.getOkToProducer());
        System.out.println(Thread.currentThread().getName() + " ---->StringStorage size is "+ buffer.getStringStorageSize());
        // the condition of okToProducer = true keep the consumer threads stay inside this while block till the producer shut it down by turning the switch off
        while (/*buffer.hasPendingLines()*/ buffer.getStringStorageSize()>0 || buffer.getOkToProducer()) { // two situations apply: 1 is when producer is within run(), 2 is when okToProducer is false, but buffer.sizse >0
            String line = buffer.get();
            sb.append(line + "\n");
        }
        processDoc();
    }
    public void processDoc() {
        System.out.println("******Counter of " + Thread.currentThread().getName() + " is " + counter);
        file = new File(filePath);
       // System.out.println(" in processDoc" + filePath);
        try(FileWriter fileWriter = new FileWriter(file)){
           fileWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
