package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.SmsTaskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsTaskWorker {
    private final BaseSmsService baseSmsService;
    private final SmsCrondService smsCrondService;
    private final SmsTaskProperties taskProperties;

    public void process(SmsCrond task) {
        try {
            SmsSendResultDTO result = baseSmsService.sendTask(task);
            if (result.isOk()) {
                smsCrondService.markSuccess(task.getId(), currentTime());
                return;
            }
            handleFailure(task, result.getErrorCode(), result.getErrorMessage());
        } catch (Exception e) {
            log.error("sms task execution error, taskId={}", task.getId(), e);
            handleFailure(task, "SEND_FAIL", e.getMessage());
        }
    }

    private void handleFailure(SmsCrond task, String errorCode, String errorMessage) {
        int now = currentTime();
        short retryCount = (short) (task.getRetryCount() + 1);
        String message = errorCode + ": " + (errorMessage == null ? "unknown error" : errorMessage);
        if (isRetryable(errorCode) && retryCount <= taskProperties.getMaxRetryCount()) {
            int nextRetryTime = now + taskProperties.getRetryDelaySeconds() * retryCount;
            smsCrondService.markRetryWait(task.getId(), retryCount, nextRetryTime, message, now);
            return;
        }
        smsCrondService.markFinalFailed(task.getId(), retryCount, message, now);
    }

    private boolean isRetryable(String errorCode) {
        return "SEND_FAIL".equals(errorCode) || "biz.FREQUENCY".equals(errorCode);
    }

    private int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
