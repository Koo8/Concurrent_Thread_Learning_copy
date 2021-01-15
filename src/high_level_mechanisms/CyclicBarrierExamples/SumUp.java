package high_level_mechanisms.CyclicBarrierExamples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SumUp {

    public static void main(String[] args) {
        int count = 0;
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        CyclicBarrier barrier = new CyclicBarrier(3, new GetSum(list));
        Stream.generate(()->new Thread(new SectionSumUp(barrier,list,0)))
                .limit(3)
                .collect(Collectors.toList())
                .forEach(Thread::start);
    }
}

class SectionSumUp implements Runnable{
    CyclicBarrier barrier;
    List<Integer> list;
    int num;
    SectionSumUp (CyclicBarrier barrier, List<Integer> list, int num){
        this.barrier = barrier;
        this.list = list;
        this.num =num;
    }

    @Override
    public void run() {
        for (int i = 1+num; i <= 10+num; i++) {
            list.add(i);
        }
        System.out.println(Thread.currentThread().getName() + " : list size : "+list.size());
        try {
            barrier.await(); // wait for other threads to finished add numbers
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class GetSum implements Runnable {
    List<Integer> list;
    int sum;
    GetSum(List<Integer> list){
        this.list = list;
        sum = 0;
    }

    @Override
    public void run() {
        for(int i:list){
           sum+=i;
        }
        System.out.println("Total sum is " + sum);

    }
}
