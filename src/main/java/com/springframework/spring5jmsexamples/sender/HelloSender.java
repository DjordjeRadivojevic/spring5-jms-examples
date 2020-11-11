package com.springframework.spring5jmsexamples.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springframework.spring5jmsexamples.config.JmsConfig;
import com.springframework.spring5jmsexamples.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate =  2000)
    public void sendMessages(){
        System.out.println("I am sending a messages");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .messages("Hello World!")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
        System.out.println("Message Sent");
    }

    @Scheduled(fixedRate = 2000)
    public void sendandReceiveMessage() throws JMSException {
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .messages("Hello")
                .build();
        Message receviedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, session -> {
            Message helloMessage;
            try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMessage.setStringProperty("_type", "com.springframework.spring5jmsexamples.model.HelloWorldMessage");

                System.out.println("Sending Hello");
                return helloMessage;
            } catch (JsonProcessingException e) {
                throw new JMSException("boom");
            }
        });

        System.out.println(receviedMsg.getBody(String.class));

    }
}
