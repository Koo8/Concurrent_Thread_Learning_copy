package high_level_mechanisms.phraser_package;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the operation of searching for files with a determined
 * extension modified in the last 24 hours in a folder and its subfolders
 */
public class FileSearch implements Runnable {
    //store the folder in which the search operation will begin.
    private String initPath;
    //store the extension of the files we are going to look for.
    private String end;
    //store the full path of the files we will find with the desired
    //characteristics.
    private List <String> results;

    private Phaser phaser;

    public FileSearch (String initPath, String end, Phaser phaser) {
        this.initPath = initPath;
        this.end = end;
        this.phaser = phaser;
        this.results = new ArrayList<String>();
    }

    @Override
    public void run () {
        //The search won't begin until all the threads have been created.
        phaser.arriveAndAwaitAdvance ();  // since in main() the number of phase is set to 3, this line of code does as register(),into 1st phase
        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());
        File file = new File (initPath);
        // check file feature, add file to result list
        if (file.isDirectory ()) {
            directoryProcess (file);
        }
        // the following code will implement checkResults
        if (checkResults ()) { // into 2nd phase
            return;
        }
        filterResults (); // remove unqualified file path from the list
        if (checkResults ()) {  // into 3rd phase
            return;
        }
        showInfo ();
        phaser.arriveAndDeregister ();
        System.out.printf ("%s: Work completed.\n", Thread.currentThread().getName ());
    }

    /**
     * It receives a File object as a parameter and it processes all its files
     * and subfolders.
     *
     * @param file
     */
    private void directoryProcess (File file) {
        File[] files = file.listFiles ();
        if (files!= null) {
            for (File value : files) {
                if (value.isDirectory()) {
                    directoryProcess(value);
                } else {
                    fileProcess(value);
                }
            }
        }
    }

    /**
     * checks if its extension is equal to the one we are looking for
     *
     * @param file
     */
    private void fileProcess (File file) {
        if (file.getName (). endsWith (end)) {
            results.add (file.getAbsolutePath ());
        }
    }

    /**
     * deleting the files that were modified more than 24 hours ago
     */
    private void filterResults () {
        List <String> newResults = new ArrayList <String> ();
        long actualDate = new Date().getTime();
        for (String result : results) {
            File file = new File(result);
            long fileDate = file.lastModified();
            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1,
                    TimeUnit.DAYS)) {
                newResults.add(result);
            }
        }
        results = newResults;
    }

    private boolean checkResults () {
        if (results.isEmpty ()) {
            System.out.printf ("checkResults-->empty--> %s: Phase %d: 0 results. END \n", Thread.currentThread ().getName(), phaser.getPhase ());
//            System.out.printf ("%s: Phase %d: End.\n", Thread.currentThread ().getName(), phaser.getPhase ());
            //stop phaser
            phaser.arriveAndDeregister ();
            return true;
        } else {
            System.out.printf ("checkResults==>not empty==> %s: Phase %d: %d results.\n", Thread.currentThread ().getName(), phaser.getPhase (), results.size ());
            //this thread has finished the actual
            //phase and it wants to be blocked until all the participant threads in the phased
            //operation finish the actual phase.
            phaser.arriveAndAwaitAdvance ();
            return false;
        }
    }

    private void showInfo () {
        for (String result : results) {
            File file = new File(result);
            System.out.printf("%s:  %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        phaser.arriveAndAwaitAdvance ();
    }

    public static void main (String [] args) throws Exception {
        Phaser phaser = new Phaser (3);   // the parties number of 3 corresponding with arriveAndAwaitAdvance() to serve as register() with the phaser
        FileSearch system = new FileSearch ("C:\\Windows", "log", phaser);
        FileSearch apps = new FileSearch ("C:\\Program Files", "log", phaser);
        FileSearch documents = new FileSearch ("C:\\ProgramData", "log", phaser);

        Thread systemThread = new Thread (system, "System");
        Thread appsThread = new Thread (apps, "appsThread");
        Thread documentsThread = new Thread (documents, "documentsThread");

        systemThread.start ();
        appsThread.start ();
        documentsThread.start ();

        systemThread.join ();
        appsThread.join ();
        documentsThread.join ();

        System.out.println ("Terminated:" + phaser.isTerminated ());
    }
}
