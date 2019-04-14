package com.example.demo.provider;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

public class FifoQueue implements QueueProvider {

    private final String queueUrl;
    private final AmazonSQS sqs;

    public FifoQueue(AmazonSQS sqs, String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    public List<Message> receiveMessages() {
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(queueUrl);

        receiveMessageRequest.setReceiveRequestAttemptId("1");
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }

    public void deleteMessage(Message message) {

            final String messageReceiptHandle = message.getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(queueUrl,
                    messageReceiptHandle));

    }
}
