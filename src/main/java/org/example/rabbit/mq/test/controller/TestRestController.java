package org.example.rabbit.mq.test.controller;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class TestRestController {

    private RabbitTemplate rabbitTemplate;

    public TestRestController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping(
            path = "/produce-and-forget"
    )
    @ResponseStatus(value = HttpStatus.OK)
    public void produceAndForget(final Person person){
        final String activityId = UUID.randomUUID().toString();
        System.out.println("TestRestController produceAndForget() " + activityId);
        rabbitTemplate.convertAndSend(
                "simple-test-queue",
                person,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setMessageId(activityId);
                    messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    messageProperties.setHeader(
                            "pdrCallDt",
                            LocalDateTime
                                    .now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
                    );
                    return message;
                }
        );
    }

    @GetMapping(
            path = "/request-response-queue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.OK)
    public PersonResponseDto queueRequestResponseSemanticTest(final Person person){
        String activityId = UUID.randomUUID().toString();
        System.out.println("TestRestController requestResponseTestQueue() activityId " + activityId);

        PersonResponseDto personResponseDto = rabbitTemplate.convertSendAndReceiveAsType(
                "request-response-queue",
                person,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setHeader(
                            "pdrCallDt",
                            LocalDateTime
                                    .now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
                    );
                    messageProperties.setMessageId(activityId);
                    messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    return message;
                },
                new ParameterizedTypeReference<>() {}
        );

        System.out.println("TestRestController requestResponseTestQueue() personResponseDto "+personResponseDto);
        return personResponseDto;
    }
}
