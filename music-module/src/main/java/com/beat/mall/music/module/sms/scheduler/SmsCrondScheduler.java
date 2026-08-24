package com.beat.mall.music.module.sms.scheduler;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.SmsTaskProperties;
import com.beat.mall.music.module.sms.service.SmsCrondService;
import com.beat.mall.music.module.sms.service.SmsTaskWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Component
@ConditionalOnProperty(prefix = "app.sms.task", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SmsCrondScheduler {
    private final SmsCrondService smsCrondService;
    private final SmsTaskWorker smsTaskWorker;
    private final SmsTaskProperties taskProperties;
    @Qualifier("smsTaskExecutor")
    private final Executor smsTaskExecutor;

    @Scheduled(fixedDelayString = "${app.sms.task.scan-interval-ms:5000}", initialDelayString = "${app.sms.task.initial-delay-ms:5000}")
    public void scanAndDispatch() {
        int now = currentTime();
        smsCrondService.recoverExpiredLease(now);
        String claimToken = UUID.randomUUID().toString();
        List<SmsCrond> tasks = smsCrondService.claimReadyTasks(
                taskProperties.getNodeId(),
                claimToken,
                now,
                now + taskProperties.getLeaseSeconds(),
                taskProperties.getClusterBatchSize()
        );
        for (SmsCrond task : tasks) {
            try {
                smsTaskExecutor.execute(() -> smsTaskWorker.process(task, claimToken));
            } catch (RejectedExecutionException e) {
                smsCrondService.returnToPending(task.getId(), claimToken, "发送线程池繁忙", currentTime());
                log.warn("sms task executor rejected, taskId={}", task.getId());
            }
        }
    }

    private int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
