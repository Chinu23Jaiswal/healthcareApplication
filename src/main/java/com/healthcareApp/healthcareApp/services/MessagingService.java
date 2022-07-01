package com.healthcareApp.healthcareApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessagingService<T> {
    @Autowired
    private KafkaTemplate<String, T> kafkaTemplate;

    public void sendMessageToTopic(String topic, T t) {
        log.info("sendMessageToTopic executed");
        Message<T> message = MessageBuilder
                .withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        this.kafkaTemplate.send(message);
    }

}
