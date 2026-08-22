package com.beat.mall.music.module.sms.task;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.music.module.sms.service.BaseSmsService;
import com.beat.mall.music.module.sms.service.SmsCrondService;
import com.beat.mall.music.module.sms.service.SmsLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsTaskExecutionService {

    private final ObjectMapper objectMapper;
    private final SmsCrondService smsCrondService;
    private final BaseSmsService baseSmsService;
    private final SmsLogService smsLogService;

    @Value("${app.sms.kafka.max-retries:3}")
    private int maxRetries;

    @Value("${app.sms.kafka.retry-delay-seconds:60}")
    private int retryDelaySeconds;

    @KafkaListener(topics = "${app.sms.kafka.topic}", groupId = "${app.sms.kafka.consumer-group}")
    public void execute(String payload) throws JsonProcessingException {
        SmsTaskMessage message = objectMapper.readValue(payload, SmsTaskMessage.class);
        if (message.getTaskId() == null) {
            throw new IllegalArgumentException("短信任务消息缺少taskId");
        }

        int now = currentTime();
        if (!smsCrondService.markSending(message.getTaskId(), now)) {
            log.info("skip processed sms task, taskId={}", message.getTaskId());
            return;
        }

        SmsSendResultDTO result = baseSmsService.doSend(message.getPhone(), message.getContent());
        int attemptCount = message.getRetryCount() == null ? 1 : message.getRetryCount() + 1;
        smsLogService.saveLog(message.getTaskId(), message.getPhone(),
                baseSmsService.buildSmsContent(message.getContent()), result,
                BaseSmsService.SEND_TYPE_CROND, attemptCount);

        short retryCount = (short) attemptCount;
        if (result.isOk()) {
            smsCrondService.markSendSuccess(message.getTaskId(), retryCount, now);
            return;
        }

        boolean retryExhausted = attemptCount >= maxRetries;
        short status = (short) (retryExhausted ? 4 : 0);
        Integer nextRetryTime = retryExhausted ? null : now + retryDelaySeconds;
        smsCrondService.markSendFailed(message.getTaskId(), status, retryCount,
                nextRetryTime, result.getErrorMessage(), now);
        log.warn("sms task execution failed, taskId={}, attemptCount={}, retryExhausted={}",
                message.getTaskId(), attemptCount, retryExhausted);
    }

    private int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
