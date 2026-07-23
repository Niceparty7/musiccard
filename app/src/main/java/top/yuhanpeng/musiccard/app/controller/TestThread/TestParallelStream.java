package top.yuhanpeng.musiccard.app.controller.TestThread;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
class Sale {
    private String productId;  // 商品
    private long quantity;  // 销量
    private double amount;     // 销售额
}

public class TestParallelStream {
    public static void main(String[] args) {
        // mock50万条数据
        List<Sale> sales = IntStream.rangeClosed(1, 500000)
                .mapToObj(i -> new Sale(
                        "P" + (i % 1000),              // 1000种商品
                        ThreadLocalRandom.current().nextInt(1, 100),
                        ThreadLocalRandom.current().nextDouble(10, 1000)))
                .collect(Collectors.toList());

        // 单线汇总 (常规 stream)
        long t1 = System.nanoTime();
        Map<String, DoubleSummaryStatistics> single =
                sales.stream()
                        .collect(Collectors.groupingBy(
                                Sale::getProductId,
                                Collectors.summarizingDouble(s -> s.getAmount())));
        long t2 = System.nanoTime();
        System.out.printf("单线耗时：%.2f ms%n", (t2 - t1) / 1_000_000.0);

        //并行汇总
        long t3 = System.nanoTime();
        Map<String, DoubleSummaryStatistics> parallel =
                sales.parallelStream()
                        .collect(Collectors.groupingBy(
                                Sale::getProductId,
                                Collectors.summarizingDouble(Sale::getAmount)));
        long t4 = System.nanoTime();
        System.out.printf("parallelStream 耗时：%.2f ms%n", (t4 - t3) / 1_000_000.0);
    }
}