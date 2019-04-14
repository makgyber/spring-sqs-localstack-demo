package com.example.demo.useCase;


import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.example.demo.provider.FifoQueue;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

public class ConsumeQueueUseCase {
    FifoQueue fifo;
    AmazonSQS sqs;

//    @Value("queueUrl")
    String queueUrl = "http://localhost:4576/queue/payment_instructions.fifo";;

    public ConsumeQueueUseCase() {

        sqs =  AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://localhost:4576", "REGION")
                ).build();


        fifo = new FifoQueue(sqs, queueUrl);
    }

    public void handle() {

        List<Message> messages = fifo.receiveMessages();
        for (final Message message : messages) {
            System.out.println("Message");
            System.out.println("  MessageId:     "
                    + message.getMessageId());
            System.out.println("  ReceiptHandle: "
                    + message.getReceiptHandle());
            System.out.println("  MD5OfBody:     "
                    + message.getMD5OfBody());
            System.out.println("  Body:          "
                    + message.getBody());
            for (final Map.Entry<String, String> entry : message.getAttributes()
                    .entrySet()) {
                System.out.println("Attribute");
                System.out.println("  Name:  " + entry.getKey());
                System.out.println("  Value: " + entry.getValue());
            }
            fifo.deleteMessage(message);
        }

    }
}
