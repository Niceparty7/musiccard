package top.yuhanpeng.musiccard.app.controller.testthread.threadpoolexample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestWorkStealingPool {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newWorkStealingPool(3);
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
            });
        }
        Thread.sleep(1000);

        pool.shutdown();
    }
}