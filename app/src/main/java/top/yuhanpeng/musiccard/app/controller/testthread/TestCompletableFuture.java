package top.yuhanpeng.musiccard.app.controller.testthread;

/**
 * 多源数据接口伪代码
 */

/*
@RestController
@RequestMapping("/index")
@RequiredArgsConstructor
@Slf4j
public class Demo {
    private final ThreadPoolExecutor pool;

    public IndexVO getIndexPage() {
        CompletableFuture<BannerVO> bannerFuture = CompletableFuture.supplyAsync(() -> bannerClient.getBanner(), pool);
        CompletableFuture<CategoryVO> categoryFuture = CompletableFuture.supplyAsync(() -> categoryClient.getCategory(), pool);
        CompletableFuture<ChannelVO> channelFuture = CompletableFuture.supplyAsync(() -> channelClient.getChannel(), pool);
        CompletableFuture<AcitivityVO> activityFuture = CompletableFuture.supplyAsync(() -> activityClient.getActivity(), pool);
        CompletableFuture.allOf(
                bannerFuture,
                categoryFuture,
                channelFuture,
                activityFuture
        ).exceptionally(ex -> {
            log.error("异步任务异常", ex);
            return null;
        }).join();
        IndexVO indexVO = new IndexVO()
                .setBanner(bannerFuture.join())
                .setCategory(categoryFuture.join())
                .setChannel(channelFuture.join())
                .setActivity(activityFuture.join());
        return indexVO;
    }
}
 */