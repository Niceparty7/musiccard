package com.beat.mall.music.module.sms.service;

import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import com.beat.mall.music.module.sms.mapper.SmsForbidMapper;
import com.beat.mall.music.module.sms.mapper.SmsUseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SmsSendGuardService {

    private final SmsUseMapper smsUseMapper;
    private final SmsForbidMapper smsForbidMapper;
    private final AliyunSmsProperties properties;

    @Transactional
    public boolean tryAcquire(String phone) {
        int now = currentTime();
        if (smsForbidMapper.countActive(phone, now) > 0) {
            return false;
        }

        int minuteStart = now - now % 60;
        smsUseMapper.increment(phone, minuteStart, now);
        Integer requestCount = smsUseMapper.selectRequestCount(phone, minuteStart);
        if (requestCount != null && requestCount >= properties.getMinuteLimitPerPhone()) {
            smsForbidMapper.insertOrExtend(
                    phone,
                    now,
                    now + properties.getForbidSeconds(),
                    "一分钟内短信请求次数达到限制",
                    now
            );
            return false;
        }
        return true;
    }

    private int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
