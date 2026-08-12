package com.beat.mall.module.sms.scheduler;

import com.beat.mall.module.sms.domain.SmsSendResultDTO;
import com.beat.mall.module.sms.entity.SmsCrond;
import com.beat.mall.module.sms.service.BaseSmsService;
import com.beat.mall.module.sms.service.SmsCrondService;
import com.beat.mall.module.sms.service.SmsLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 定时任务：扫描 sms_crond 表中 status=0 的待发送任务，真正调用阿里云 PNVS 发送短信，
 * 发送完成后更新任务状态：1-发送成功，2-发送失败。
 * 这是"异步发送"需求的真正发送环节：接口只负责创建任务，此定时任务负责执行发送。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsCrondScheduler {

    private final SmsCrondService smsCrondService;
    private final BaseSmsService baseSmsService;
    private final SmsLogService smsLogService;
    private final Executor smsExecutor;

    @Scheduled(fixedDelay = 30_000, initialDelay = 5_000)
    public void scanAndSend() {
        try {
            List<SmsCrond> pending = smsCrondService.selectPending(100);
            if (pending.isEmpty()) {
                return;
            }
            log.info("scan pending sms crond count = {}", pending.size());
            List<CompletableFuture<Void>> fs = pending.stream()
                    .map(t -> CompletableFuture.runAsync(() -> handleOne(t), smsExecutor))
                    .toList();
            CompletableFuture.allOf(fs.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("sms crond scan error", e);
        }
    }

    private void handleOne(SmsCrond crond) {
        // crond.content 存的是验证码；sms_log.content 记录整条短信内容
        String smsContent = baseSmsService.buildSmsContent(crond.getContent());
        SmsSendResultDTO r = baseSmsService.doSend(crond.getPhone(), crond.getContent());
        smsLogService.saveLog(crond.getPhone(), smsContent, r, BaseSmsService.SEND_TYPE_CROND);
        crond.setStatus((short) (r.isOk() ? 1 : 2));
        crond.setErrorMessage(r.getErrorMessage());
        crond.setSendTime((int) (System.currentTimeMillis() / 1000));
        crond.setUpdateTime(crond.getSendTime());
        crond.setRetryCount((short) (crond.getRetryCount() + 1));
        smsCrondService.update(crond);
    }
}