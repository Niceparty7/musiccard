package com.beat.mall.music.module.sms.quartz;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobFactoryConfig {
    @Bean
    public SchedulerFactoryBeanCustomizer quartzJobFactoryCustomizer(AutowireCapableBeanFactory beanFactory) {
        return schedulerFactoryBean -> schedulerFactoryBean.setJobFactory(new AutowiringQuartzJobFactory(beanFactory));
    }
}