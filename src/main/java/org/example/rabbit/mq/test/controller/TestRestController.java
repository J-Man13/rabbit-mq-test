package org.example.rabbit.mq.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestRestController {
    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;

    public TestRestController(RabbitTemplate rabbitTemplate,
                              ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping(
            path = "/simple-test-queue"
    )
    @ResponseStatus(value = HttpStatus.OK)
    public void send(final Person person) throws JsonProcessingException {
        String activityId = UUID.randomUUID().toString();
        System.out.println("TestRestController " + activityId);

        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsString(person).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8")
                .setMessageId(activityId)
                .build();

        rabbitTemplate.convertAndSend(
                "simple-test-queue",
                message
        );

    }

}
