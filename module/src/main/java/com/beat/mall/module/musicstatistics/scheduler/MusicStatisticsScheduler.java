package com.beat.mall.module.musicstatistics.scheduler;

import com.beat.mall.module.musicstatistics.service.MusicStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 音乐统计定时任务：每天凌晨 2 点统计全部音乐数量并写入 music_statistics 表
 * cron: 0 0 2 * * ?  → 秒 分 时 日 月 周（每天 02:00:00）
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MusicStatisticsScheduler {

    private final MusicStatisticsService musicStatisticsService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void run() {
        try {
            musicStatisticsService.dailyStatistic();
            log.info("music statistic done");
        } catch (Exception e) {
            log.error("music statistic error", e);
        }
    }
}