package top.yuhanpeng.musiccard.app.controller.testthread.threadpoolexample;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThreadPoolExecutor {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy()
        );
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
            });
        }
        pool.shutdown();
    }
}
