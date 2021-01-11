package FileMockPackageFromCuncurrentCookbook;

public class Consumer implements Runnable{
    private Buffer buffer;
    //private StringBuilder sb;
    // this has to be the local variable of run() to capture all the readings from all threads
    // private String lineTotal= null;
    public Consumer (Buffer buffer) {
        this.buffer = buffer;
        //  sb = new StringBuilder();
    }
    @Override
    public void run() {
        System.out.println("@@@" + Thread.currentThread().getName() + " in run() ");
        String lineTotal = null;// highlight: must be local variable, for capturing all readings by all threads, it will loss all content beyond the run block
        StringBuilder sb = new StringBuilder();
        // the condition of okToProducer = true keep the consumer threads stay inside this while block till the producer shut it down by turning the switch off
        while ( buffer.getStringStorageSize()>0 || buffer.getOkToProducer()) { // two situations apply: 1 is when producer is within run(), 2 is when okToProducer is false, but buffer.sizse >0
            lineTotal = buffer.get();
            // System.out.println(" &&&&& new Line added "+ lineTotal);
            sb.append(lineTotal + "\n");
        }
        System.out.println("#### \n" + sb);
        // processDoc(lineTotal);
    }
    public void processDoc(String line) {
        // System.out.println("******Counter of " + Thread.currentThread().getName() + " is " + counter);
//        file = new File(filePath);
        // System.out.println(" in processDoc" + filePath);
        System.out.println("%%%------\n" +line );
    }
}
