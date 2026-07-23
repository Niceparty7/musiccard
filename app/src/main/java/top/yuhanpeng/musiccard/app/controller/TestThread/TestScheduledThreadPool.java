package top.yuhanpeng.musiccard.app.controller.TestThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestScheduledThreadPool {

    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.schedule(() -> {
                        System.out.println(Thread.currentThread().getName() + "执行任务：" + num);

                    }, 3,
                    TimeUnit.SECONDS);
        }
        System.out.println("------------------------------------------");
        for (int i = 0; i < 10; i++) {
            int num = i;
            pool.scheduleAtFixedRate(() -> {
                        System.out.println(Thread.currentThread().getName() + "执行任务：" + num);

                    }, 3,
                    1,
                    TimeUnit.SECONDS);
        }
        pool.shutdown();
    }
}