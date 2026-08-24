package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsCompensationProperties;
import com.beat.mall.music.module.sms.service.SmsCompensationService;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SmsKafkaCompensationJobTest {

    @Test
    void shouldRunCompensationWhenEnabled() {
        SmsCompensationService service = mock(SmsCompensationService.class);
        SmsCompensationProperties properties = new SmsCompensationProperties();
        properties.setEnabled(true);
        SmsKafkaCompensationJob job = new SmsKafkaCompensationJob();
        job.setCompensationService(service);
        job.setProperties(properties);

        job.execute(mock(JobExecutionContext.class));

        verify(service).compensateOnce();
    }

    @Test
    void shouldSkipPersistedJobWhenDisabled() {
        SmsCompensationService service = mock(SmsCompensationService.class);
        SmsCompensationProperties properties = new SmsCompensationProperties();
        properties.setEnabled(false);
        SmsKafkaCompensationJob job = new SmsKafkaCompensationJob();
        job.setCompensationService(service);
        job.setProperties(properties);

        job.execute(mock(JobExecutionContext.class));

        verifyNoInteractions(service);
    }
}
