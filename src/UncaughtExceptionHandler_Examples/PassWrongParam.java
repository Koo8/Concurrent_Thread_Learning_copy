package UncaughtExceptionHandler_Examples;

public class PassWrongParam {
    public static void main(String[] args) {
        Task task = new Task();
        Thread thread = new Thread(task);
        thread.setUncaughtExceptionHandler(new MyExceptionHandler());
        thread.start();
    }

    private static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("Inside the task ...");
            int number = Integer.parseInt("TTT");
        }
    }

    private static class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("An exception has been captured\n");
            System.out.printf("Thread: %s\n",t.getId());
            System.out.printf("Exception: %s: %s\n",
                    e.getClass().getName(),e.getMessage());
            System.out.printf("Stack Trace: \n");
            e.printStackTrace(System.out);
            System.out.printf("Thread status: %s\n",t.getState());
        }
    }
}
