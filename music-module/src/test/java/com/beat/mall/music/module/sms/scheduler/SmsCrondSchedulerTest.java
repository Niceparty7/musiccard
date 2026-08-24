package com.beat.mall.music.module.sms.scheduler;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.SmsTaskProperties;
import com.beat.mall.music.module.sms.service.SmsCrondService;
import com.beat.mall.music.module.sms.service.SmsTaskWorker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsCrondSchedulerTest {

    @Test
    void shouldDispatchOnlyTaskClaimedSuccessfully() {
        SmsCrondService crondService = mock(SmsCrondService.class);
        SmsTaskWorker worker = mock(SmsTaskWorker.class);
        SmsCrond task = new SmsCrond().setId(1L);
        when(crondService.selectReadyTasks(anyInt(), eq(20))).thenReturn(List.of(task));
        when(crondService.markSending(eq(1L), anyInt())).thenReturn(true);
        SmsTaskProperties properties = new SmsTaskProperties();
        properties.setScanLimit(20);
        Executor directExecutor = Runnable::run;

        new SmsCrondScheduler(crondService, worker, properties, directExecutor).scanAndDispatch();

        verify(worker).process(task);
    }
}
