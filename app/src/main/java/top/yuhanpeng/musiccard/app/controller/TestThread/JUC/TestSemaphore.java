package top.yuhanpeng.musiccard.app.controller.TestThread.JUC;

import java.util.concurrent.*;

public class TestSemaphore {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy()
        );
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.submit(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            });
        }
        pool.shutdown();
    }
}
