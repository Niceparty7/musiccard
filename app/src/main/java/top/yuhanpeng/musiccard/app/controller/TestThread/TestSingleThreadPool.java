package top.yuhanpeng.musiccard.app.controller.TestThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSingleThreadPool {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "执行任务：" + num);
            });
        }
        pool.shutdown();
    }
}