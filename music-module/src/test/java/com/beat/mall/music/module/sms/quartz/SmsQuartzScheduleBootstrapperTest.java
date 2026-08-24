package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsQuartzProperties;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsQuartzScheduleBootstrapperTest {

    @Test
    void shouldCreateScheduleWhenItDoesNotExist() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        SmsQuartzProperties properties = new SmsQuartzProperties();
        properties.setJobName("smsDispatchJob");
        properties.setTriggerName("smsDispatchTrigger");
        properties.setGroup("SMS");
        when(scheduler.checkExists(TriggerKey.triggerKey("smsDispatchTrigger", "SMS"))).thenReturn(false);
        when(scheduler.checkExists(JobKey.jobKey("smsDispatchJob", "SMS"))).thenReturn(false);

        new SmsQuartzScheduleBootstrapper(scheduler, properties).run(null);

        verify(scheduler).addJob(any(), eq(false));
        verify(scheduler).scheduleJob(any(Trigger.class));
    }
}
