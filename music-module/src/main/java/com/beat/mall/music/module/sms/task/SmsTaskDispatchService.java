package com.beat.mall.music.module.sms.task;

import com.beat.mall.common.entity.sms.SmsCrond;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SmsTaskDispatchService {

    private final KafkaTemplate<String, String> taskTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.sms.kafka.topic}")
    private String taskChannel;

    public CompletableFuture<SendResult<String, String>> dispatch(SmsCrond task) {
        SmsTaskMessage message = new SmsTaskMessage(
                task.getId(), task.getMessageId(), task.getPhone(),
                task.getContent(), task.getRetryCount());
        try {
            return taskTemplate.send(taskChannel, task.getMessageId(),
                    objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
