package high_level_mechanisms.phraser_package;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 3 threads search 3 directories, count total numbers, do filtering to get only files
 * that's been modified within 24 hours, do counting total number again, display the file
 * path and total found number
 */

public class FileSearch_Copy {

    public static void main(String[] args) {


        // phaser used by all threads
        Phaser phaser = new Phaser(3); // registered 3 parites, not arrived yet
        SearchingFile searchDriver = new SearchingFile(phaser, "C:\\drivers", "log");
        SearchingFile searchingFile = new SearchingFile(phaser, "C:\\Program Files", "log");
        SearchingFile searchingProject = new SearchingFile(phaser, "C:\\ProgramData", "log");

        Thread threadWindow = new Thread(searchDriver, "Driver");
        Thread threadFile = new Thread(searchingFile, "File");
        Thread threadProject = new Thread(searchingProject, "Project");

       // Result result = new Result();

        threadWindow.start();
        threadFile.start();
        threadProject.start();

        try {
            threadWindow.join();
            threadFile.join();
            threadProject.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Phaser is terminated." + phaser.isTerminated());
    }
}

class SearchingFile implements Runnable{
    Phaser phaser;
    String filePath;
    String extension;
    List<String> filesFound = new ArrayList<>();// each directory is counted individually, so no need for synchronized list


    public SearchingFile(Phaser phaser, String filePath, String extension) {
        this.phaser = phaser;
        this.filePath = filePath;
        this.extension = extension;
    }

    @Override
    public void run() {
        // Calling this method at the beginning of the run() method ensures that none of the FileSearch
        //threads begin their job until all the threads are created.
        phaser.arriveAndAwaitAdvance(); // put all threads at the same start
       // System.out.println(Thread.currentThread().getName() + " : into phase "+ phaser.getPhase());
        File file = new File(filePath);
        // phase 1: to go thru file index for "log" files count
        toLookThruFile(file);
        // phase 2: if empty, deregister, otherwise into phase 2
        // highlight: make the method return boolean and choose "true" to return
        if(toCheckIfEmpty()) return;
        //in phase 2, filter the result
        toFilterResult();
        // phase 3, checkIfEmpty again
        if(toCheckIfEmpty()) return;
        // in phase 3, display the result
        //toDisplayResult();
        // deregister the phaser
        phaser.arriveAndDeregister();
        System.out.println(Thread.currentThread().getName() + " has completed the job.");
    }

    private void toDisplayResult() {
//        for(String f: filesFound){
//            System.out.println(Thread.currentThread().getName() + ": " + f);
//        }
        System.out.println(Thread.currentThread().getName() + " has total "+ filesFound.size() + " files with " + extension );

    }

    private List<String> toFilterResult() {
        List<String> newList = new ArrayList<>();
        long currentDate = new Date().getTime();//Returns the number of milliseconds since January 1, 1970,
        for(String f:filesFound){
            File file = new File(f);
            long lastModified = file.lastModified();
            if(currentDate - lastModified < TimeUnit.MILLISECONDS.convert(1,TimeUnit.DAYS)) {
              // filesFound.remove(f); // the list is messed up
                // highlight: must use a new list to collect new file, loop through itself and remove some will mess up the function
                newList.add(f);
            }

        }
        System.out.println(phaser.getPhase() + " phaser, After filtering, there are "+ newList.size() + " files in the list in " + Thread.currentThread().getName() );
        return newList;
    }
    // return boolean so that when call this method, we can use "return" to end the thread when true is returned
    private boolean toCheckIfEmpty() {
        if(filesFound.isEmpty()){
            //deregister the phaser to terminate this thread
            phaser.arriveAndDeregister();
            System.out.println(Thread.currentThread().getName() + " is deRegistered ");
            return true;  // true for ending this thread
        } else {
            phaser.arriveAndAwaitAdvance(); // into next phase.
            return false;
        }

    }
    // phase 1
    private void toLookThruFile(File file) {

         if(file.isDirectory()){
             processDirectory(file);
             System.out.println(phaser.getPhase() + "  phase:  there are "+ filesFound.size() + " files founded  by " + Thread.currentThread().getName() );
         } else {
             processFile(file);
         }
    }
    // helper method for "toLookThruFile()"
    private void processFile(File file) {
       // System.out.println("In processFile... in "+ Thread.currentThread().getName());
       
        //if(file != null){
            if(file.getName().endsWith(extension)) {
               // System.out.println("&&&  "+file.getName() + " is ended with the extension");
                filesFound.add(file.getAbsolutePath());
                //System.out.println("PROCESSFILE : now there are "+ filesFound.size() + " files founded  by " + Thread.currentThread().getName() );
            }
        //}

    }

    // helper method for "toLookThruFile()"
    private void processDirectory(File file) {
        File[] files = file.listFiles();
        // highLight: remove null situation to avoid exception
        if(files != null) {
            for (File f : files) {

              //  if (f != null) {
                    // System.out.println(f.getName() + " is being looped through in "+ Thread.currentThread().getName() );
                    if (f.isDirectory()) {
                        processDirectory(f);
                        // System.out.println(Thread.currentThread().getName() + " :: " + f.getName() + " is a directory, keep on looping.");
                    } else {
                        processFile(f);
                        // System.out.println(Thread.currentThread().getName() + " ==> the file " + f.getName() + " is NOT a directory");
                    }
               // }

            }
        }

    }
}
