package UncaughtExceptionHandler_Examples;
/**
 * Advanced exception handling in a thread -> uncaughtExceptionHandler, an inner class of thread
 * that catch those unexpected scenarios where our code would come to a halt, but instead offer a
 * solution.
 */

public class DivisionByZero {

    public static void main(String[] args) throws Exception { // "throws Exception" part is added when "throw new Exception" code is added in the body
        Thread.setDefaultUncaughtExceptionHandler(new DivisionException());
        // test 1:
       // System.out.println("Division by zero -> " + 10/0);  // throwable is arithmeticException: divide by zero
        // test 2:
        throw new Exception("Please redo the math.");  // throwable is modified to "please redo the math"

    }
    // Thread is the enclosing class of UncaughtExceptionHandler interface
    private static class DivisionException implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Oops! Division by zero is not allowed ->" + e);
        }
    }
}
