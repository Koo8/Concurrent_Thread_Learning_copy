package SynchronizedKeyword;

/**
 *  --Even with volatile on "cash" variable, it is still a race condition. Car and motor numbers are messed up.
 *  --Since \/*oooooo*\/ methods are accessed by all sensors, so by adding "synchronized" keyword to each method
 *  will solve the race condition.
 *  -- use new Object() as the lock to create synchronized block as part of the method implementation, instead
 *  of synchronize the whole method.
 *  . The objective is to have the critical section (the block of code
 * that can be accessed only by one thread at a time) as short as possible.
 */

public class RaceCondition_ParkingIncome {

    public static void main(String[] args) {
        ParkingAccounting accounting = new ParkingAccounting();
        VehicleInAndOutAlgorithm algorithm = new VehicleInAndOutAlgorithm(accounting);
        // decide how many threads to run parking sensor
        int numberOfSensor = Runtime.getRuntime().availableProcessors() * 2;
        Thread[] threads = new Thread[numberOfSensor];
        for (int i = 0; i < numberOfSensor; i++) {
            // create runnable then thread
            ParkingSensor sensor = new ParkingSensor(algorithm); // all sensors reporting to the same "algorithm";
            Thread thread = new Thread(sensor, ""+i);
            thread.start();
            threads[i] = thread;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i <threads.length ; i++) {
             try{
                 threads[i].join();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
        }



        // display total revenue:
        accounting.toGetTotalParkingRevenue();
        System.out.println("Main: Number of cars " + algorithm.numOfCar);
        System.out.println("Main: Number of motors "+ algorithm.numOfMotorCycle);


    }

    // inner class - to decide parking payment, calculate total income
    private static class ParkingAccounting {
        private static final int cost = 2;
        private /*volatile */long cash;  // highlight: the volatile is not helping here, it's the "Topaywhenexit" that needs to be guarded

        ParkingAccounting() {
            cash = 0;
        }
        /*oooooo*/
        private synchronized void  toPayWhenExit() { // each vehicle needs to pay $2 when leaving the parking lot
            cash += cost;
        }

        private void toGetTotalParkingRevenue() {
            // todo: check this block, I wrote differently from the book
            System.out.println("ParkingAccounting: Total revenue is " + cash);
        }
    }
    // define the algorithm to calculate cars in the parking and out of the parking and how much needs to be collected each event
    private static class VehicleInAndOutAlgorithm {
        ParkingAccounting accounting;
        private long numOfCar;
        private long numOfMotorCycle;
        private Object controlCars, controlMotors; // add these locks for synchronized blocks

        VehicleInAndOutAlgorithm(ParkingAccounting accounting) {
            this.accounting = accounting;
            numOfCar = 0;
            numOfMotorCycle = 0;
            controlCars = new Object();
            controlMotors = new Object();
        }
        /*oooooo*/
        private /*synchronized*/ void carEnterParking() {
            synchronized (controlCars ){
                numOfCar++;
            }

        }
        /*oooooo*/
        private /*synchronized*/ void motorEnterParking() {
            synchronized (controlMotors) {
                numOfMotorCycle++;
            }

        }
        /*oooooo*/
        private /*synchronized */void carExit() {
           // add synchronized block to first line only.
            synchronized (controlCars) {
                numOfCar--;
            }
            accounting.toPayWhenExit();
        }
        /*oooooo*/
        private /*synchronized */ void motorExit() {
            // add synchronized block to first line only.
            synchronized (controlMotors){
                numOfMotorCycle--;
            }
            accounting.toPayWhenExit();
        }

    }
    // parking sensor to detect car and motor movement
    private static class ParkingSensor implements Runnable {

        VehicleInAndOutAlgorithm algorithm;

        ParkingSensor(VehicleInAndOutAlgorithm algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public void run() {
             // each sensor detect 10 times of 3 cars in and 3 cars out
            try {
                for (int i = 0; i < 10; i++) {
                    algorithm.carEnterParking();
                    algorithm.carEnterParking();
                    // NOTE: if this time is larger than line 18 the thread sleep time, then no race condition occurred, if smaller, then it occurs;
                    Thread.sleep(200);
                    algorithm.motorEnterParking();
                    Thread.sleep(50);
                    algorithm.carExit();
                    algorithm.carExit();
                    algorithm.motorExit();
                }
                System.out.printf("Sensor: %s Thread -> %d cars and %d motors are in the parking lot\n",Thread.currentThread().getName(), algorithm.numOfCar, algorithm.numOfMotorCycle);
                System.out.printf("Sensor: %s Thread -> Total parking fees collected is %d\n",Thread.currentThread().getName(), algorithm.accounting.cash);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
