package FutureExamples.ForkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * This program compares sequential and parallel computing speed difference
 * For a 10000000000 size array of integer, find the largest of all.
 * OUTCOME: forkjoinpool taks a lot longer time.
 */

public class MaxNumber {

    public static void main(String[] args) {
        // create the array
        int[] numArray = new int[100000000]; //
        //sequential computation
        for (int i = 0; i < numArray.length; i++) {
           numArray[i] = (int)(Math.random()*1000);
           // System.out.println(numArray[i]);
        }
        long pt = System.currentTimeMillis();
        //System.out.println(pt);
        int max = doCalculation(numArray,0,numArray.length);

        long pt1 = System.currentTimeMillis();
        //System.out.println(pt1);
        System.out.println("Sequential :  max is  " + max + " for " + (pt1 - pt) ) ;

        //parallel calculation

        ForkJoinPool pool = new ForkJoinPool();// use runtime.availableProcessor()
        long paraTime = System.currentTimeMillis();
        FindMax task = new FindMax(numArray, 0, numArray.length);
        int result = pool.invoke(task);
        System.out.println("Parallel : max is " + result + " for " + (System.currentTimeMillis()-paraTime));

    }


    static int doCalculation(int[] numArray, int start, int length) {
        int max = 0;
        for (int i = start; i <length ; i++) {
            if(max < numArray[i] ){
                max = numArray[i];
            }
        }
        return max;
    }
}

class FindMax extends RecursiveTask<Integer> {
    int[] array;
    int threshold = 100;
    int start, end;
    int max = 0;

    public FindMax(int[] numArray, int start, int end) {
        this.array = numArray;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int max = 0;
        if ((end- start )< 100) {
         max =  MaxNumber.doCalculation(array, start, end);
        } else {
            int middle = (end + start) /2;
            FindMax task1 = new FindMax(array, start, middle);
            FindMax task2 = new FindMax(array, middle, end);
            task1.fork();
            task2.fork(); // to ask pool to implement
            int r1 = task1.join();
            int r2 = task2.join();

            max = (r1 > r2)? r1 : r2;
        }
        return max;
    }
}
