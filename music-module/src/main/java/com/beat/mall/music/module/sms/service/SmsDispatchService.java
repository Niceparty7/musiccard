package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.SmsTaskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsDispatchService {
    private final SmsCrondService smsCrondService;
    private final SmsTaskWorker smsTaskWorker;
    private final SmsTaskProperties taskProperties;
    @Qualifier("smsTaskExecutor")
    private final Executor smsTaskExecutor;

    public void dispatchOnce() {
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
