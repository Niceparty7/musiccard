package com.beat.mall.music.module.sms.task;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SmsTaskChannelConfig {

    @Bean
    public NewTopic smsTaskChannel(
            @Value("${app.sms.kafka.topic}") String taskChannel,
            @Value("${app.sms.kafka.partitions:1}") int partitions,
            @Value("${app.sms.kafka.replicas:1}") int replicas) {
        return TopicBuilder.name(taskChannel)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
