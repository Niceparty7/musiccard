package com.beat.mall.music.module.sms.service;

import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import com.beat.mall.common.api.sms.SmsTaskSubmitResultDTO;
import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseSmsService {

    public static final int SEND_TYPE_SYNC = 1;
    public static final int SEND_TYPE_BATCH = 2;
    public static final int SEND_TYPE_ASYNC = 3;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AsyncClient pnvsClient;
    private final AliyunSmsProperties props;
    private final SmsLogService smsLogService;
    private final SmsSendGuardService smsSendGuardService;
    private final SmsTaskProducer smsTaskProducer;
    private final SmsKafkaProperties smsKafkaProperties;
    @Qualifier("smsExecutor")
    private final Executor smsExecutor;

    /**
     * 同步发送单个手机号
     * 验证码由后台生成，无需前端传入
     */
    public SmsSendResultDTO sendSync(String phone) {
        if (!smsSendGuardService.tryAcquire(phone)) {
            return SmsSendResultDTO.fail("SMS_FORBIDDEN", "短信请求过于频繁，请一小时后重试");
        }
        String code = randomCode6();
        String smsContent = buildSmsContent(code);
        SmsSendResultDTO result = doSend(phone, code);
        smsLogService.saveLog(phone, smsContent, result, SEND_TYPE_SYNC);
        return result;
    }

    /**
     * 多线程批量发送：每个手机号生成独立验证码，并发下发
     */
    public List<SmsSendResultDTO> sendBatch(List<String> phones) {
        List<CompletableFuture<SmsSendResultDTO>> futures = new ArrayList<>();
        for (String phone : phones) {
            if (!smsSendGuardService.tryAcquire(phone)) {
                futures.add(CompletableFuture.completedFuture(
                        SmsSendResultDTO.fail("SMS_FORBIDDEN", phone + " 短信请求过于频繁，请一小时后重试")));
                continue;
            }
            String code = randomCode6();
            String smsContent = buildSmsContent(code);
            futures.add(CompletableFuture.supplyAsync(() -> {
                SmsSendResultDTO result = doSend(phone, code);
                smsLogService.saveLog(phone, smsContent, result, SEND_TYPE_BATCH);
                return result;
            }, smsExecutor));
        }
        return futures.stream().map(CompletableFuture::join).toList();
    }

    /** 仅完成频控和 Kafka 任务发布，实际发送由 Kafka 消费者异步执行。 */
    public SmsTaskSubmitResultDTO submitAsyncTask(String phone) {
        if (!smsSendGuardService.tryAcquire(phone)) {
            return SmsTaskSubmitResultDTO.rejected("SMS_FORBIDDEN", "短信请求过于频繁，请一小时后重试");
        }
        if (!smsKafkaProperties.isEnabled()) {
            return SmsTaskSubmitResultDTO.rejected("SMS_ASYNC_DISABLED", "短信异步任务通道未开启");
        }
        long taskId = IdWorker.getId();
        SmsTaskMessage message = new SmsTaskMessage()
                .setVersion("1.0")
                .setTaskId(taskId)
                .setPhone(phone)
                .setVerifyCode(randomCode6())
                .setCreatedAt(System.currentTimeMillis())
                .setTraceId(UUID.randomUUID().toString());
        try {
            smsTaskProducer.publish(message);
            return new SmsTaskSubmitResultDTO()
                    .setAccepted(true)
                    .setTaskId(taskId)
                    .setStatus("PENDING");
        } catch (Exception e) {
            log.error("sms kafka task publish failed, taskId={}", taskId, e);
            return SmsTaskSubmitResultDTO.rejected("MQ_PUBLISH_FAILED", "短信任务提交失败");
        }
    }

    /**
     * 真正调 PNVS SDK：content 为后台生成的 6 位验证码，填入模板 ${code} 变量
     * 返回 SmsSendResultDTO（含 verifyCode，便于调用方/前端展示）
     */
    public SmsSendResultDTO doSend(String phone, String content) {
        // 防御：content 非 6 位数字时兜底重新生成
        String code = (content != null && content.matches("\\d{6}")) ? content : randomCode6();
        SendSmsVerifyCodeRequest req = SendSmsVerifyCodeRequest.builder()
                .phoneNumber(phone)
                .signName(props.getSignName())
                .templateCode("100001")
                .templateParam("{\"code\":\"" + code + "\",\"min\":\"2\"}")
                .build();
        try {
            SendSmsVerifyCodeResponse resp = pnvsClient.sendSmsVerifyCode(req).get();
            String respCode = resp.getBody().getCode();
            String msg = resp.getBody().getMessage();
            if ("OK".equals(respCode)) {
                return new SmsSendResultDTO()
                        .setOk(true)
                        .setVerifyCode(code)
                        .setCode(respCode)
                        .setMessage(msg)
                        .setRequestId(resp.getBody().getRequestId());
            } else {
                log.error("pnvs sms send fail, phone={}, respCode={}, message={}", phone, respCode, msg);
                return SmsSendResultDTO.fail(respCode, msg);
            }
        } catch (Exception e) {
            log.error("pnvs sms send exception, phone={}", phone, e);
            return SmsSendResultDTO.fail("SEND_FAIL", e.getMessage());
        }
    }

    /**
     * 生成 6 位数字验证码
     */
    private String randomCode6() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    /**
     * 构造发送的整条短信内容（与模板 100001 渲染结果一致）
     * 格式：【签名】您验证码为123456，尊敬的客户，以上验证码5分钟内有效，请注意保密，切勿告知他人。
     */
    public String buildSmsContent(String code) {
        return "【" + props.getSignName() + "】您验证码为" + code + "，尊敬的客户，以上验证码5分钟内有效，请注意保密，切勿告知他人。";
    }

}
