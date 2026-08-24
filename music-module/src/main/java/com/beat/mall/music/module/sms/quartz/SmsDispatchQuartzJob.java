package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsQuartzProperties;
import com.beat.mall.music.module.sms.service.SmsDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SmsDispatchQuartzJob implements Job {
    private SmsDispatchService smsDispatchService;
    private SmsQuartzProperties quartzProperties;

    @Autowired
    public void setSmsDispatchService(SmsDispatchService smsDispatchService) {
        this.smsDispatchService = smsDispatchService;
    }

    @Autowired
    public void setQuartzProperties(SmsQuartzProperties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    @Override
    public void execute(JobExecutionContext context) {
        if (!quartzProperties.isEnabled()) {
            log.debug("sms quartz job skipped because app.sms.quartz.enabled=false");
            return;
        }
        smsDispatchService.dispatchOnce();
    }
}