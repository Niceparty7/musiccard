package com.beat.mall.module.musicstatistics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@Slf4j
public class SchedulerThreadPoolConfig {

    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);                       // 调度线程数
        scheduler.setThreadNamePrefix("sched-");
        scheduler.setErrorHandler(t ->                 // 任务异常兜底，避免静默失败
                log.error("scheduled task error", t));
        scheduler.initialize();
        return scheduler;
    }
}