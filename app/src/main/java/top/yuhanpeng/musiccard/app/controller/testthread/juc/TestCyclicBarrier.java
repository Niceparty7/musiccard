package top.yuhanpeng.musiccard.app.controller.testthread.juc;

import java.util.concurrent.*;

public class TestCyclicBarrier {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy()
        );
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        pool.shutdown();
    }
}
