package top.yuhanpeng.musiccard.app.controller.TestThread.JUC;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy()
        );
        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        pool.shutdown();
    }
}
