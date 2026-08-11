package com.beat.mall.module.sms.service;

import com.beat.mall.module.sms.entity.SmsCrond;
import com.beat.mall.module.sms.mapper.SmsCrondMapper;
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

    public SmsCrond getById(Long id) {
        return smsCrondMapper.getById(id);
    }
}