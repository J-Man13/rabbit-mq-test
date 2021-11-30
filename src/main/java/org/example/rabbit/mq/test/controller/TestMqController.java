package org.example.rabbit.mq.test.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TestMqController {

    private RabbitTemplate rabbitTemplate;

    public TestMqController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "simple-test-queue",concurrency = "1")
    public void consumeMessageAndForget(final Person person, final Message message){
        String activityId = message.getMessageProperties().getMessageId();
        String pdrCallDtString = message.getMessageProperties().getHeader("pdrCallDt");
        LocalDateTime pdrCallDt = LocalDateTime.parse(
                pdrCallDtString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        );
        System.out.println("TestMqController consumeMessageAndProduceIntoAnotherQueue() " + activityId+" "+
                "Producer call date time "+pdrCallDt);
        System.out.println("TestMqController " + activityId);
        System.out.println("TestMqController " + person);
    }

    @RabbitListener(queues = "request-response-queue",concurrency = "1")
    public PersonResponseDto consumeMessageAndProduceResponse(final Person person, final Message message){
        String activityId = message.getMessageProperties().getMessageId();
        String pdrCallDtString = message.getMessageProperties().getHeader("pdrCallDt");
        LocalDateTime pdrCallDt = LocalDateTime.parse(
                pdrCallDtString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        );
        System.out.println("TestMqController consumeMessageAndProduceIntoAnotherQueue() " + activityId+" "+
                "Producer call date time "+pdrCallDt);
        System.out.println("TestMqController consumeMessageAndProduceIntoAnotherQueue() " + person);

        PersonResponseDto personResponseDto = PersonResponseDto.builder()
                .activityId(activityId)
                .name(person.getName())
                .age(person.getAge()*2)
                .localDateTime(LocalDateTime.now())
                .build();

        return personResponseDto;
    }
}
