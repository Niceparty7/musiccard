package com.beat.mall.module.sms.service;

import com.beat.mall.module.sms.domain.SmsSendResultDTO;
import com.beat.mall.module.sms.entity.SmsLog;
import com.beat.mall.module.sms.mapper.SmsLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsLogService {

    private final SmsLogMapper smsLogMapper;

    public Long saveLog(String phone, String content, SmsSendResultDTO result, int sendType) {
        int now = (int) (System.currentTimeMillis() / 1000);
        SmsLog log = new SmsLog()
                .setPhone(phone)
                .setContent(content)
                .setResult(result.isOk() ? "OK" : "FAIL")
                .setBizId(result.getBizId())
                .setRequestId(result.getRequestId())
                .setErrorCode(result.getErrorCode())
                .setErrorMessage(result.getErrorMessage())
                .setSendType(sendType)
                .setStatus(result.isOk() ? 1 : 2)
                .setSendTime(now)
                .setCreateTime(now)
                .setUpdateTime(now);
        return smsLogMapper.insert(log);
    }

    public Long countByPhoneToday(String phone, Integer start, Integer end) {
        return smsLogMapper.countByPhoneToday(phone, start, end);
    }
}