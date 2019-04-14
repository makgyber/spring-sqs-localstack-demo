package com.example.demo.provider;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true, services = { "sqs" })
public class FifoQueueTest {
    AmazonSQS sqs;
    String myQueueUrl;

    private void initialize() {
        sqs = DockerTestUtils.getClientSQS();

        final Map<String, String> attributes = new HashMap<>();

        attributes.put("FifoQueue", "true");
        attributes.put("ContentBasedDeduplication", "true");

        final CreateQueueRequest createQueueRequest =
                new CreateQueueRequest("MyFifoQueue.fifo")
                        .withAttributes(attributes);
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

        for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);
        }

        // Send a message.
        System.out.println("Sending a message to MyFifoQueue.fifo.\n");
        final SendMessageRequest sendMessageRequest =
                new SendMessageRequest(myQueueUrl,
                        "This is my message text.");

        sendMessageRequest.setMessageGroupId("messageGroup1");

        final SendMessageResult sendMessageResult = sqs
                .sendMessage(sendMessageRequest);
        final String sequenceNumber = sendMessageResult.getSequenceNumber();
        final String messageId = sendMessageResult.getMessageId();
    }

    @Test
    public void testReceiveAndDeleteMessage() {
        initialize();
        FifoQueue fifo = new FifoQueue(sqs, myQueueUrl);
        List<Message> messages = fifo.receiveMessages();
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("This is my message text.", messages.get(0).getBody());
        fifo.deleteMessage(messages.get(0));
    }

}