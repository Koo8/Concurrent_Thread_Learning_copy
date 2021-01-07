package FileSearchInterrupted;

public class FindFile_Main {

    public static void main(String[] args) {
        SearchFile task = new SearchFile("inputFiles.lst", "C:\\Users\\NancyPC\\Documents\\NetBeansProjects");

        Thread thread = new Thread(task);
        thread.start();
        try {
           Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();  // this interrupt should be throw and caught by the thread.
    }
}
