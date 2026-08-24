package com.beat.mall.music.module.sms.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(prefix = "app.sms.kafka", name = "enabled", havingValue = "true")
public class SmsKafkaTopicConfig {

    @Bean
    public NewTopic smsSendTopic(SmsKafkaProperties properties) {
        return TopicBuilder.name(properties.getTopic()).partitions(6).replicas(1).build();
    }

    @Bean
    public NewTopic smsDeadLetterTopic(SmsKafkaProperties properties) {
        return TopicBuilder.name(properties.getDltTopic()).partitions(6).replicas(1).build();
    }
}
