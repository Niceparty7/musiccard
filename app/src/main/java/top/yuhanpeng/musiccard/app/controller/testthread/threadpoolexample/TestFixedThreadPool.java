package top.yuhanpeng.musiccard.app.controller.testthread.threadpoolexample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestFixedThreadPool {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
            });
        }
        pool.shutdown();
    }
}