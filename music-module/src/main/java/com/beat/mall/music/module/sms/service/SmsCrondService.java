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

    public Long create(SmsCrond task) {
        smsCrondMapper.insert(task);
        return task.getId();
    }

    public List<SmsCrond> selectReadyTasks(int now, int limit) { return smsCrondMapper.selectReadyTasks(now, limit); }
    public boolean markSending(Long id, int now) { return smsCrondMapper.markSending(id, now) == 1; }
    public void markSuccess(Long id, int now) { smsCrondMapper.markSuccess(id, now); }
    public void markRetryWait(Long id, short retryCount, int nextRetryTime, String errorMessage, int now) {
        smsCrondMapper.markRetryWait(id, retryCount, nextRetryTime, errorMessage, now);
    }
    public void markFinalFailed(Long id, short retryCount, String errorMessage, int now) {
        smsCrondMapper.markFinalFailed(id, retryCount, errorMessage, now);
    }
    public void returnToPending(Long id, String errorMessage, int now) { smsCrondMapper.returnToPending(id, errorMessage, now); }
    public void recoverTimeoutSending(int deadline, int now) { smsCrondMapper.recoverTimeoutSending(deadline, now); }
}
