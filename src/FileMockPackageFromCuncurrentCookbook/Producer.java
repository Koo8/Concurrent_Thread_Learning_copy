package FileMockPackageFromCuncurrentCookbook;

public class Producer implements Runnable {
    private FileMock mock;
    private Buffer buffer;
    public Producer (FileMock mock, Buffer buffer){
        this.mock = mock;
        this.buffer = buffer;
    }
    @Override
    public void run() {
        buffer.setOkToProducer(true);
        System.out.println("***" +Thread.currentThread().getName() +" -> okToProducer is " +buffer.getOkToProducer());
        while (mock.hasMoreLines()){ // this block will run till mockFile has not more lines left
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setOkToProducer(false); // this helps to stop all consumers to wait
        System.out.println("***" +Thread.currentThread().getName() +" -> okToProducer is " +buffer.getOkToProducer());
    }
}
