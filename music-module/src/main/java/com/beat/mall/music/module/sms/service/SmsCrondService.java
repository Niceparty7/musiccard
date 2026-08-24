package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.mapper.SmsCrondMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsCrondService {
    private final SmsCrondMapper smsCrondMapper;

    public Long create(SmsCrond task) {
        smsCrondMapper.insert(task);
        return task.getId();
    }

    @Transactional
    public List<SmsCrond> claimReadyTasks(String nodeId, String claimToken, int now, int leaseExpireTime, int limit) {
        int claimed = smsCrondMapper.claimReadyTasks(nodeId, claimToken, now, leaseExpireTime, limit);
        return claimed == 0 ? List.of() : smsCrondMapper.selectClaimedTasks(nodeId, claimToken);
    }
    public void markSuccess(Long id, String claimToken, int now) { smsCrondMapper.markSuccess(id, claimToken, now); }
    public void markRetryWait(Long id, short retryCount, String claimToken, int nextRetryTime, String errorMessage, int now) {
        smsCrondMapper.markRetryWait(id, retryCount, claimToken, nextRetryTime, errorMessage, now);
    }
    public void markFinalFailed(Long id, short retryCount, String claimToken, String errorMessage, int now) {
        smsCrondMapper.markFinalFailed(id, retryCount, claimToken, errorMessage, now);
    }
    public void returnToPending(Long id, String claimToken, String errorMessage, int now) {
        smsCrondMapper.returnToPending(id, claimToken, errorMessage, now);
    }
    public void recoverExpiredLease(int now) { smsCrondMapper.recoverExpiredLease(now); }
}
