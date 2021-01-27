package FutureExamples.ForkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * calculate a big double[], break down to smaller tasks, then combine the result
 * use RecursiveTask and forkJoinPool, recursiveTasks are Future, which means
 * it can get(), isDone() and cancel()
 */

public class SumUp {
    public static void main(String[] args) {
        // create a double[]
        Double[] data = new Double[500];
        for (int i = 0; i <data.length ; i++) {
            data[i] = i *1.0; //(double)(((i % 2)== 0 ) ? i : -1);
        }
        DoSum task = new DoSum(data, 0, data.length);
        ForkJoinPool pool = new ForkJoinPool();
        double total = pool.invoke(task);
        System.out.println(total);
    }

}

class DoSum extends RecursiveTask<Double>  {
    Double[] data;
    int start, end;
    final int threshold = 100;
    double sum = 0;
    public DoSum(Double[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Double compute() {

        if((end - start) < threshold) {
            System.out.println("in compute: start is " + start + " , end is " + end);
            // do sectional calculation
            for (int i = start; i < end; i++) {
                sum+=data[i];
            }
            System.out.println("The sectional sum is " + sum);
        }  else {
            // break down into half to see if meed the threshold for sectional calculation
            int middleIndex = (end -start)/2 + start; // NOTE: must "+start" to adjust the middleIndex  ==> (start + end)/2
            System.out.println("Middle index is  "+ middleIndex);

            DoSum section1 = new DoSum(data,start, middleIndex);
            DoSum section2 = new DoSum(data,middleIndex, end);
            section1.fork(); // fork() -> execute this task in the pool
            section2.fork();
            sum = section1.join() + section2.join(); // join() -> return the result of compute();
        }
       // System.out.println(sum);
        return sum;
    }
}
