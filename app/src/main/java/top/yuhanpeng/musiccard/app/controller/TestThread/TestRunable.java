package top.yuhanpeng.musiccard.app.controller.TestThread;

import java.util.ArrayList;
import java.util.List;


public class TestRunable implements Runnable {

    private List<Integer> list;
    private StringBuffer sb;

    public TestRunable(List<Integer> list, StringBuffer stringBuilder) {
        this.list = list;
        this.sb = stringBuilder;
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        StringBuffer sb = new StringBuffer();
        TestRunable t1 = new TestRunable(list, sb);
        TestRunable t2 = new TestRunable(list, sb);
        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t1);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("最终长度:" + sb.length()
        );
    }

    @Override
    public void run() {
        for (Integer i : list) {
            sb.append(i);
        }
        System.out.println(Thread.currentThread().getName() + " " + sb.length());
    }
}