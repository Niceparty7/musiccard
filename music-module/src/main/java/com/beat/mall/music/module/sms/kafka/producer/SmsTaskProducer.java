package com.beat.mall.music.module.sms.kafka.producer;

import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SmsTaskProducer {
    private final KafkaTemplate<String, SmsTaskMessage> kafkaTemplate;
    private final SmsKafkaProperties properties;

    public void publish(SmsTaskMessage message) {
        send(properties.getTopic(), message);
    }

    public void publishToDeadLetter(SmsTaskMessage message) {
        send(properties.getDltTopic(), message);
    }

    private void send(String topic, SmsTaskMessage message) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Kafka短信任务通道未开启");
        }
        try {
            kafkaTemplate.send(topic, message.getPhone(), message)
                    .get(properties.getPublishTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka短信任务发布失败", e);
        } catch (Exception e) {
            throw new IllegalStateException("Kafka短信任务发布失败", e);
        }
    }
}
