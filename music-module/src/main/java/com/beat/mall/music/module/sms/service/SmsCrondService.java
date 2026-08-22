package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.mapper.SmsCrondMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsCrondService {

    private final SmsCrondMapper smsCrondMapper;

    public Long insert(SmsCrond crond) {
        return smsCrondMapper.insert(crond);
    }

    public Integer update(SmsCrond crond) {
        return smsCrondMapper.update(crond);
    }

    public List<SmsCrond> selectPending(int limit) {
        return smsCrondMapper.selectPending(limit);
    }

    public List<SmsCrond> selectPendingPublish(int limit) {
        return smsCrondMapper.selectPendingPublish(limit);
    }

    public boolean markSending(Long id, Integer updateTime) {
        return smsCrondMapper.markSending(id, updateTime) == 1;
    }

    public void markPublishSuccess(Long id, Integer updateTime) {
        smsCrondMapper.markPublishSuccess(id, updateTime);
    }

    public void markPublishFailed(Long id, String errorMessage, Integer nextRetryTime, Integer updateTime) {
        smsCrondMapper.markPublishFailed(id, errorMessage, nextRetryTime, updateTime);
    }

    public void markSendSuccess(Long id, Short retryCount, Integer sendTime) {
        smsCrondMapper.markSendSuccess(id, retryCount, sendTime);
    }

    public void markSendFailed(Long id, Short status, Short retryCount, Integer nextRetryTime,
                               String errorMessage, Integer updateTime) {
        smsCrondMapper.markSendFailed(id, status, retryCount, nextRetryTime, errorMessage, updateTime);
    }

    public SmsCrond getById(Long id) {
        return smsCrondMapper.getById(id);
    }
}
