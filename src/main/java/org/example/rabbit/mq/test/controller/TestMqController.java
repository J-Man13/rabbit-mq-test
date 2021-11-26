package org.example.rabbit.mq.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Component
public class TestMqController {

    private ObjectMapper objectMapper;

    public TestMqController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "simple-test-queue")
    public void receiveMessage(final Message message) throws JsonProcessingException{
        String activityId = message.getMessageProperties().getMessageId();
        System.out.println("TestMqController " + activityId);
        String mqMessageBodyAsString = new String(message.getBody(), StandardCharsets.UTF_8);
        Person person =  objectMapper.readValue(mqMessageBodyAsString,Person.class);
        System.out.println("TestMqController " + person);
    }
}
