package org.example.rabbit.mq.test.controller;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class TestMqController {

    @RabbitListener(queues = "simple-test-queue")
    public void consumeMessageAndForget(final Person person,
                                        final @Header(AmqpHeaders.MESSAGE_ID) String activityId,
                                        final @Header("pdrCallDt") String pdrCallDtString){
        LocalDateTime pdrCallDt = LocalDateTime.parse(
                pdrCallDtString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        );
        System.out.println("TestMqController consumeMessageAndProduceIntoAnotherQueue() " + activityId+" "+
                "Producer call date time "+pdrCallDt);
        System.out.println("TestMqController consumeMessageAndForget()" + person);
    }

    @RabbitListener(queues = "request-response-queue")
    public PersonResponseDto consumeMessageAndProduceResponse(final Person person,
                                                              final @Header(AmqpHeaders.MESSAGE_ID) String activityId,
                                                              final @Header("pdrCallDt") String pdrCallDtString){
        LocalDateTime pdrCallDt = LocalDateTime.parse(
                pdrCallDtString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        );
        System.out.println("TestMqController consumeMessageAndProduceResponse() " + activityId+" "+
                "Producer call date time "+pdrCallDt);
        System.out.println("TestMqController consumeMessageAndProduceResponse() " + person);

        PersonResponseDto personResponseDto = PersonResponseDto.builder()
                .activityId(activityId)
                .name(person.getName())
                .age(person.getAge()*2)
                .localDateTime(LocalDateTime.now())
                .build();

        return personResponseDto;
    }
}
