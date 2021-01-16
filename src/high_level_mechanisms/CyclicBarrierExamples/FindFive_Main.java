package high_level_mechanisms.CyclicBarrierExamples;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * This copies MatrixMock program
 * cyclicBarrier is similar to countDownLatch, both needs to wait for all threads to be finished before proceeding
 * to next step, however, cyclicBarrier has reset() that can restart the whole process or define a recovery route
 */
public class FindFive_Main {
    static final int row = 100;
    static final int column = 10;
    static final int paticipatingRows = row/5;

    public static void main(String[] args) throws InterruptedException {

        MatrixFactory matrixFactory = new MatrixFactory(row, column);
        CounterArray counterArray = new CounterArray(row);
        // create 5 threads of task to search "5" in each sub-matrix
        SearchTask[] tasks = new SearchTask[5];
        Thread[] threads = new Thread[5];
        if(matrixFactory.start == true){ // NOTE: matrixFactory has to be built up first before using it as an parameter
            CyclicBarrier barrier = new CyclicBarrier(5,new GetTotalCount(matrixFactory, counterArray));

            for (int i = 0; i < tasks.length; i++) {
                tasks[i]= new SearchTask(matrixFactory,i*paticipatingRows,(i+1)*paticipatingRows,barrier, counterArray);
                threads[i] = new Thread(tasks[i]);
            }
            for (int i = 0; i <5 ; i++) {
                threads[i].start();
            }
        }
    }
}

/**
 * create a random matrix of row X column
 */
class MatrixFactory {
    int row, column, counter;
    int[][] matrix;
    Random random = new Random();
    boolean start = false;
    public MatrixFactory(int row, int column) {
        counter = 0;
        this.row = row;
        this.column = column;
        matrix = new int[row][column];
        fillInTheMatrix();
        // check result
        System.out.println(counter + " times of 5 in matrix just created.");
        start = true;
    }

    private void fillInTheMatrix() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j <column ; j++) {
                int newNum = random.nextInt(10);
                matrix[i][j] = newNum;
                if(newNum == 5)counter++;
            }
        }
    }

    int[] getRow(int i){
        if(i>=0 && i<matrix.length) {
            return matrix[i];
        }
        return null;
    }

    int getNumber (int row, int column){
        return matrix[row][column];
    }
}

/**
 * search sub-matrix between row of startIndex and endIndex for number 5;
 * use cyclicBarrier to await() for all subTasks to be finished
 */
class SearchTask implements Runnable {
    MatrixFactory matrixFactory;
    int startIndex, endIndex, counter;
    CyclicBarrier barrier;
    CounterArray counterArray;

    public SearchTask(MatrixFactory matrixFactory, int startIndex, int endIndex, CyclicBarrier barrier, CounterArray counterArray) {
        this.matrixFactory = matrixFactory;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.barrier = barrier;
        counter = 0;
        this.counterArray = counterArray;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
//            System.out.println(Thread.currentThread().getName() + " is checking from" +
//                    " " + startIndex+ " to " + endIndex);
//            System.out.println(Thread.currentThread().getName() + " the matrixFactory is  " + matrixFactory.counter);
            int[] row  = matrixFactory.getRow(i);
            //System.out.println(Thread.currentThread().getName() + " the ROW is "+ row);
            int rowCounter = 0;
            for (int j = 0; j <row.length; j++) {
               int num = matrixFactory.getNumber(i,j);
               if(num == 5) {
                   rowCounter++;
                   counter++;
               }
            }
            counterArray.setCounterNumberForRowIndex(i,rowCounter);

        }
        System.out.println(Thread.currentThread().getName() + " found " + counter + " times of 5 in this subMatrix.");
        try {
            barrier.await(); // wait for all subTasks to be done.
        } catch (InterruptedException e) {
            // highlight: if one thread is interrupted, other threads will throw brokenBarrierExceptions
            System.out.println(Thread.currentThread().getName() + " is interrupted");
            e.printStackTrace();
        }  // highlight: when reset() is used, this exception will be thrown.
        catch (BrokenBarrierException e) {
            System.out.println(Thread.currentThread().getName() + " is broken" + barrier.isBroken());
            // recovering process
            // or restarting the process
            e.printStackTrace();
        }
    }
}

/**
 * An auxiliary class for collecting all data from each sub tasks guarded by cyclicBarrier
 */
class CounterArray {
    int[] counterArray;
    CounterArray(int num){
        counterArray = new int[num];
    }
    void setCounterNumberForRowIndex(int row, int counter){
        counterArray[row] = counter;
    }
    int[] getCounterArray(){
        return counterArray;
    }
}


class GetTotalCount implements Runnable {
    MatrixFactory matrixFactory;
    CounterArray counterArray;
    int totalCount = 0;

    public GetTotalCount(MatrixFactory matrixFactory, CounterArray counterArray) {
        this.matrixFactory = matrixFactory;
        this.counterArray = counterArray;
    }

    @Override
    public void run() {
        toSumUp();
        System.out.println("Total " + totalCount + " times of 5 appeared");
    }

    private int toSumUp() {
        for(int i:counterArray.getCounterArray()) {
            totalCount+=i;
        }
        return totalCount;
    }
}
