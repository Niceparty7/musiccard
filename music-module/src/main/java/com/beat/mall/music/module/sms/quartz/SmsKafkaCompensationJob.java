package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsCompensationProperties;
import com.beat.mall.music.module.sms.service.SmsCompensationService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@DisallowConcurrentExecution
@Slf4j
public class SmsKafkaCompensationJob implements Job {
    private SmsCompensationService compensationService;
    private SmsCompensationProperties properties;

    public void setCompensationService(SmsCompensationService compensationService) {
        this.compensationService = compensationService;
    }

    public void setProperties(SmsCompensationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void execute(JobExecutionContext context) {
        if (!properties.isEnabled()) {
            log.info("sms kafka compensation is disabled, skip persisted trigger");
            return;
        }
        compensationService.compensateOnce();
    }
}
