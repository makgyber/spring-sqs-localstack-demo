package com.example.demo.useCase.ReceiveMessage;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;

public class ReceiveMessageUseCase {

    private final AmazonSQS sqs;
    private final String queueUrl;

    public ReceiveMessageUseCase(AmazonSQS sqs, String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    public void handle() {
        // Receive messages.
        System.out.println("Receiving messages from MyFifoQueue.fifo.\n");
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(queueUrl);

        receiveMessageRequest.setReceiveRequestAttemptId("1");
        final List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
                .getMessages();
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
        }
        System.out.println("DONE");
    }

}
