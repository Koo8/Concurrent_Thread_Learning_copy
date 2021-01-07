package WaitForFinalization_Join;

public class Join_Main {
    public static void main(String[] args) {
        TaskOne task1 = new TaskOne();
        Thread thread1 = new Thread(task1);
        TaskTwo task2 = new TaskTwo();
        Thread thread2 = new Thread(task2);
        thread1.start();
        thread2.start();
        while(thread1.isAlive()){
            System.out.println(thread1.getState());
        }
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Both tasks have been finalized.");
    }
}
