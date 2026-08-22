package com.beat.mall.music.module.sms.service;

import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.entity.sms.SmsCrond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseSmsService {

    public static final int SEND_TYPE_SYNC = 1;
    public static final int SEND_TYPE_BATCH = 2;
    public static final int SEND_TYPE_CROND = 3;

    private final AsyncClient pnvsClient;
    private final AliyunSmsProperties props;
    private final SmsLogService smsLogService;
    private final SmsCrondService smsCrondService;
    private final Executor smsExecutor;

    /**
     * 同步发送单个手机号
     * 验证码由后台生成，无需前端传入
     */
    public SmsSendResultDTO sendSync(String phone) {
        String code = randomCode6();
        String smsContent = buildSmsContent(code);
        if (!allowToday(phone)) {
            SmsSendResultDTO r = SmsSendResultDTO.fail("OVER_DAILY_LIMIT", "同号当日已达上限");
            smsLogService.saveLog(phone, smsContent, r, SEND_TYPE_SYNC);
            return r;
        }
        SmsSendResultDTO r = doSend(phone, code);
        smsLogService.saveLog(phone, smsContent, r, SEND_TYPE_SYNC);
        return r;
    }

    /**
     * 多线程批量发送：每个手机号生成独立验证码，并发下发
     */
    public List<SmsSendResultDTO> sendBatch(List<String> phones) {
        List<CompletableFuture<SmsSendResultDTO>> fs = new ArrayList<>();
        for (String phone : phones) {
            String code = randomCode6();
            String smsContent = buildSmsContent(code);
            if (!allowToday(phone)) {
                fs.add(CompletableFuture.completedFuture(
                        SmsSendResultDTO.fail("OVER_DAILY_LIMIT", phone + " 当日已达上限")));
                continue;
            }
            fs.add(CompletableFuture.supplyAsync(() -> {
                SmsSendResultDTO r = doSend(phone, code);
                smsLogService.saveLog(phone, smsContent, r, SEND_TYPE_BATCH);
                return r;
            }, smsExecutor));
        }
        return fs.stream().map(CompletableFuture::join).toList();
    }

    /**
     * 提交异步任务：仅写 sms_crond（status=0），不调用 PNVS SDK，不发送短信
     * crond.content 存验证码；真正发送由 SmsCrondScheduler 定时任务完成
     */
    public Long submitAsyncTask(String phone) throws Exception {
        String code = randomCode6();
        if (!allowToday(phone)) {
            throw new RuntimeException("OVER_DAILY_LIMIT: " + phone);
        }
        int now = (int) (System.currentTimeMillis() / 1000);
        SmsCrond crond = new SmsCrond()
                .setMessageId("sms-" + UUID.randomUUID().toString().replace("-", ""))
                .setPhone(phone).setContent(code)
                .setStatus((short) 0).setRetryCount((short) 0)
                .setPublishStatus((short) 0).setNextRetryTime(null)
                .setCreateTime(now).setUpdateTime(now).setIsDeleted(0);
        smsCrondService.insert(crond);
        return crond.getId();
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
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    /**
     * 构造发送的整条短信内容（与模板 100001 渲染结果一致）
     * 格式：【签名】您验证码为123456，尊敬的客户，以上验证码5分钟内有效，请注意保密，切勿告知他人。
     */
    public String buildSmsContent(String code) {
        return "【" + props.getSignName() + "】您验证码为" + code + "，尊敬的客户，以上验证码5分钟内有效，请注意保密，切勿告知他人。";
    }

    /**
     * 短信每日发送限制
     */
    public synchronized boolean allowToday(String phone) {
        int dayStart = (int) (LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        int dayEnd = dayStart + 24 * 60 * 60;
        Long sent = smsLogService.countByPhoneToday(phone, dayStart, dayEnd);
        return sent == null || sent < props.getDailyLimitPerPhone();
    }
}
