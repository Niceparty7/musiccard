package top.yuhanpeng.musiccard.app.controller.TestThread;

import java.util.ArrayList;
import java.util.List;


public class TestController2 extends Thread {

    private List<Integer> list;
    private StringBuffer sb;

    public TestController2(List<Integer> list, StringBuffer stringBuilder) {
        this.list = list;
        this.sb = stringBuilder;
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        StringBuffer sb = new StringBuffer();
        TestController2 t1 = new TestController2(list, sb);
        TestController2 t2 = new TestController2(list, sb);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
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