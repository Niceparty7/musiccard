package com.beat.mall.music.module.sms.scheduler;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.task.SmsTaskDispatchService;
import com.beat.mall.music.module.sms.service.SmsCrondService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信任务发布器：扫描数据库中的待发布任务并投递到Kafka，不直接调用短信服务商。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsCrondScheduler {

    private final SmsCrondService smsCrondService;
    private final SmsTaskDispatchService smsTaskDispatchService;

    private final Set<Long> inFlightTasks = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedDelayString = "${app.sms.kafka.publish-interval-ms:5000}", initialDelay = 5000)
    public void publishPendingTasks() {
        try {
            List<SmsCrond> pending = smsCrondService.selectPendingPublish(100);
            for (SmsCrond task : pending) {
                publishOne(task);
            }
        } catch (Exception e) {
            log.error("sms kafka publish scan error", e);
        }
    }

    private void publishOne(SmsCrond task) {
        if (task.getId() == null || !inFlightTasks.add(task.getId())) {
            return;
        }
        smsTaskDispatchService.dispatch(task).whenComplete((result, error) -> {
            int now = (int) (System.currentTimeMillis() / 1000);
            try {
                if (error == null) {
                    smsCrondService.markPublishSuccess(task.getId(), now);
                    log.info("sms task published to kafka, taskId={}, messageId={}",
                            task.getId(), task.getMessageId());
                } else {
                    smsCrondService.markPublishFailed(task.getId(), errorMessage(error),
                            now + 60, now);
                    log.error("sms task publish to kafka failed, taskId={}", task.getId(), error);
                }
            } finally {
                inFlightTasks.remove(task.getId());
            }
        });
    }

    private String errorMessage(Throwable error) {
        String message = error.getMessage();
        return message == null ? error.getClass().getSimpleName() : message;
    }
}
