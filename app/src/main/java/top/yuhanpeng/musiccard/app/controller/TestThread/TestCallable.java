package top.yuhanpeng.musiccard.app.controller.TestThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public class TestCallable {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        StringBuffer sb = new StringBuffer();
        Callable<Void> testCallableCallable = () -> {
            for (Integer i : list) {
                sb.append(i);
            }
            System.out.println(Thread.currentThread().getName() + " " + sb.length());
            return null;
        };
        FutureTask<Void> testCallableFutureTask = new FutureTask<>(testCallableCallable);
        FutureTask<Void> testCallableFutureTask2 = new FutureTask<>(testCallableCallable);
        Thread thread1 = new Thread(testCallableFutureTask);
        Thread thread2 = new Thread(testCallableFutureTask2);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("最终长度:" + sb.length());
    }
}