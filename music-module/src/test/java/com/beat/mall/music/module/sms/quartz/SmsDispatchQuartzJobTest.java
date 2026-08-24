package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsQuartzProperties;
import com.beat.mall.music.module.sms.service.SmsDispatchService;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SmsDispatchQuartzJobTest {

    @Test
    void shouldDelegateToDispatchService() {
        SmsDispatchService dispatchService = mock(SmsDispatchService.class);
        SmsQuartzProperties properties = new SmsQuartzProperties();
        properties.setEnabled(true);
        SmsDispatchQuartzJob job = new SmsDispatchQuartzJob();
        job.setSmsDispatchService(dispatchService);
        job.setQuartzProperties(properties);

        job.execute(mock(JobExecutionContext.class));

        verify(dispatchService).dispatchOnce();
    }

    @Test
    void shouldSkipPersistedTriggerWhenQuartzIsDisabled() {
        SmsDispatchService dispatchService = mock(SmsDispatchService.class);
        SmsQuartzProperties properties = new SmsQuartzProperties();
        properties.setEnabled(false);
        SmsDispatchQuartzJob job = new SmsDispatchQuartzJob();
        job.setSmsDispatchService(dispatchService);
        job.setQuartzProperties(properties);

        job.execute(mock(JobExecutionContext.class));

        verifyNoInteractions(dispatchService);
    }
}
