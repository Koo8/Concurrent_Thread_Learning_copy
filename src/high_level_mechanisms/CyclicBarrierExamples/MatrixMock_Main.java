package high_level_mechanisms.CyclicBarrierExamples;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * This program is  to find the number of times a number appears in the number
 * matrix (using divide-and-conquer programming techniques). This matrix will
 * be divided into several subsets, and then each thread will search in a subset.
 * Once all threads have completed the search, the final task will unify these
 * results.
 */

/**
 * This matrix class is responsible for randomly generating a matrix of numbers between 1 and 10.
 */
class MatrixMock {

    private int[][] data;  // matrix -> two dimensional array

    /**
     * Constructor
     * @param  row number
     * @param  col number of columns
     * @param  number the number to find
     */
    public MatrixMock(int row, int col, int number) {

        int counter = 0;
        this.data = new int[row][col];
        Random random = new Random();

        //Fill the matrix with random numbers
        for(int i = 0; i < row; i ++) {
            for(int j = 0; j < col; j++) {
                data[i][j] = random.nextInt(10);
                if(data[i][j] == number) {
                    counter++;
                }
            }
        }

        //Print the number of times found in the matrix to the console
        System.out.printf("Mock: There are %d ocurrences of %d in " +
                "generated data.\n", counter, number);
    }

    /**
     * Return the data of a row of the matrix
     * @param  row number
     * @return        If the row exists, return the data of the row; otherwise return nul
     */
    public int[] getRow(int row) {

        if((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }
}

/**
 * Save the number of times the specified number found in each row of the matrix
 */
class Results {

    private int[] data;

    /**
     * Constructor
     * @param size
     */
    public Results(int size) {
        this.data = new int[size];
    }

    public void setData(int position, int value) {
        data[position] = value;
    }

    public int[] getData() {
        return data;
    }
}


/**
 * Find the class. Each search class is allocated a thread separately, which is a subTask.
 */
class Searcher implements Runnable {

    //Find the starting line number
    private int firstRow;
    //Find the end line number
    private int lastRow;
    //Find the matrix
    private MatrixMock matrixMock;
    //Result collection
    private Results results;
    //The number to be found
    private int number;

    private final CyclicBarrier barrier;

    /**
     * Constructor
     * @param  firstRow starting row number
     * @param  lastRow end row number
     * @param  matrixMock matrix
     * @param  results result set
     * @param  number the number to find
     * @param  barrier
     */
    public Searcher(int firstRow, int lastRow, MatrixMock matrixMock,
                    Results results, int number, CyclicBarrier barrier) {

        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.matrixMock = matrixMock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }

    @Override
    public void run() {

        int counter;

        System.out.printf("%s: Processing lines " +
                "from %d to %d.\n",Thread.currentThread().getName(), firstRow, lastRow);

        for(int i = firstRow; i < lastRow; i++) {
            int row[] = matrixMock.getRow(i);   // line i
            counter = 0;
            for(int j = 0; j < row.length; j++) {
                if(row[j] == number) {
                    counter++;
                }
            }
            results.setData(i, counter);
        }

        System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

    }
}

/**
 * Summary category.
 */
class Grouper implements Runnable {

    /**
     * Result set
     */
    private Results results;

    Grouper(Results results) {
        this.results = results;
    }

    @Override
    public void run() {

        int finalResult = 0;
        System.out.printf("Grouper: Processing results...\n");
        int data[] = results.getData();
        for(int count : data) {
            finalResult += count;
        }
        System.out.printf("Grouper: Total result: %d.\n",finalResult);
    }
}

public class MatrixMock_Main {

    public static void main(String[] args) {

        final int ROW = 10000;   //Row
        final int COL = 1000;   //Column
        final int TARGET_NUMBER = 5;    //The number to be searched
        final int PARTICIPANTS = 5;     //Number of subtasks
        final int LINES_PARTICIPANT = 2000;

        //Create matrix
        MatrixMock matrixMock = new MatrixMock(ROW, COL, TARGET_NUMBER);
        //Create result set
        Results results = new Results(ROW);
        Grouper grouper = new Grouper(results);
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);

        //Create a search class
        Searcher[] searchers = new Searcher[PARTICIPANTS];
        for(int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(i*LINES_PARTICIPANT,
                    i*LINES_PARTICIPANT + LINES_PARTICIPANT, matrixMock,
                    results, TARGET_NUMBER, barrier);

            Thread thread = new Thread(searchers[i]);
            thread.start();
        }

        System.out.printf("Main: The main thread has finished.\n");
    }
}