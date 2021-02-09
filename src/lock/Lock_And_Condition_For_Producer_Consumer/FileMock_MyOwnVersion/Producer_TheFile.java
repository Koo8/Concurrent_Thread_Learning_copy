package lock.Lock_And_Condition_For_Producer_Consumer.FileMock_MyOwnVersion;

/**
 * the task to fetch strings from theFile to theBuffer
 */
public class Producer_TheFile implements Runnable {
    FileCreated file;
    TheBuffer buffer;

    Producer_TheFile (FileCreated theFile, TheBuffer theBuffer){
        file = theFile;
        buffer = theBuffer;
    }

    @Override
    public void run() {
        // turn on the switch from the buffer for consumer threads to be active
        buffer.setAllThreadsOn(true);
        System.out.println("PRODUCER Switch ON");
        //
        while(file.hasPendingLines()){
            String string = file.getLine();
            buffer.fetchString(string);
        }

        // turn off the switch to kill all consumer threads when no more strings left in the buffer
        buffer.setAllThreadsOn(false);
        System.out.println("PRODUCER switch off");
    }
}
