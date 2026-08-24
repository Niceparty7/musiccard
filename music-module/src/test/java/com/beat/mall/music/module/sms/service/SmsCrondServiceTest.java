package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.mapper.SmsCrondMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsCrondServiceTest {

    @Test
    void shouldReturnOnlyTasksFromCurrentClaimToken() {
        SmsCrondMapper mapper = mock(SmsCrondMapper.class);
        SmsCrond task = new SmsCrond().setId(1L).setClaimToken("claim-a").setLockOwner("node-a");
        when(mapper.claimReadyTasks(eq("node-a"), eq("claim-a"), anyInt(), anyInt(), eq(20))).thenReturn(1);
        when(mapper.selectClaimedTasks("node-a", "claim-a")).thenReturn(List.of(task));

        List<SmsCrond> tasks = new SmsCrondService(mapper)
                .claimReadyTasks("node-a", "claim-a", 100, 400, 20);

        assertEquals(List.of(task), tasks);
        verify(mapper).selectClaimedTasks("node-a", "claim-a");
    }
}
