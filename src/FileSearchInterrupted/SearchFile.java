package FileSearchInterrupted;

import java.io.File;

public class SearchFile implements Runnable {
    private String fileName;
    private String folder;

    public SearchFile(String fileName, String folder) {
        this.fileName = fileName;
        this.folder = folder;
    }

    @Override
    public void run() {
        File file = new File(folder);

        if (file.isDirectory()) {
            try {
                processDirectory(file);
            }
            catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " is interrupted");
            }

        }

    }
    // for the next two methods, throw exception and will get caught when implementing these methods in run()
    private void processDirectory(File file) throws InterruptedException {
        System.out.println("in diretory process");
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    processDirectory(files[i]);
                } else {
                    processFile(files[i]);
                }
            }
        }
        if(Thread.interrupted()){   // main class has interruption set up to test this
            System.out.println("in directory intrruption block");
            throw new InterruptedException();
        }
    }

    private void processFile(File file) throws InterruptedException{
        if (file.getName().equals(fileName)) {
            System.out.println(Thread.currentThread().getName() + " : " + file.getAbsolutePath() + " is found.");
        }
        // to handle interruption - voluntarily throw exception to let the interruption being caught
        if (Thread.interrupted()) {
            System.out.println("in file interruption block");
            throw new InterruptedException();
        }

    }
}
