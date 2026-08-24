package com.beat.mall.music.module.sms.quartz;

import com.beat.mall.music.module.sms.config.SmsQuartzProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.sms.quartz", name = "enabled", havingValue = "true")
@Slf4j
public class SmsQuartzScheduleBootstrapper implements ApplicationRunner {
    private final Scheduler scheduler;
    private final SmsQuartzProperties properties;

    @Override
    public void run(ApplicationArguments args) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(properties.getJobName(), properties.getGroup());
        TriggerKey triggerKey = TriggerKey.triggerKey(properties.getTriggerName(), properties.getGroup());
        if (scheduler.checkExists(triggerKey)) {
            log.info("sms quartz trigger already exists, triggerKey={}", triggerKey);
            return;
        }
        JobDetail jobDetail = JobBuilder.newJob(SmsDispatchQuartzJob.class)
                .withIdentity(jobKey)
                .storeDurably()
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(properties.getIntervalSeconds())
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithExistingCount())
                .build();
        try {
            if (!scheduler.checkExists(jobKey)) {
                scheduler.addJob(jobDetail, false);
            }
            scheduler.scheduleJob(trigger);
            log.info("sms quartz trigger created, triggerKey={}, intervalSeconds={}",
                    triggerKey, properties.getIntervalSeconds());
        } catch (ObjectAlreadyExistsException e) {
            log.info("sms quartz schedule created by another node, triggerKey={}", triggerKey);
        }
    }
}