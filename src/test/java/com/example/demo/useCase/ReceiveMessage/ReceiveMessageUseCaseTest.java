package com.example.demo.useCase.ReceiveMessage;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.HashMap;
import java.util.Map;

@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true, services = { "sqs" })
public class ReceiveMessageUseCaseTest {

    @Test
    public void handlerTest() {
//        AmazonSQS sqs = initializeSqs();
        AmazonSQS sqs = DockerTestUtils.getClientSQS();

        try {

            // Create a FIFO queue.
            System.out.println("Creating a new Amazon SQS FIFO queue called " +
                    "MyFifoQueue.fifo.\n");
            final Map<String, String> attributes = new HashMap<>();

            // A FIFO queue must have the FifoQueue attribute set to true.
            attributes.put("FifoQueue", "true");

            /*
             * If the user doesn't provide a MessageDeduplicationId, generate a
             * MessageDeduplicationId based on the content.
             */
            attributes.put("ContentBasedDeduplication", "true");

            // The FIFO queue name must end with the .fifo suffix.
            final CreateQueueRequest createQueueRequest =
                    new CreateQueueRequest("MyFifoQueue.fifo")
                            .withAttributes(attributes);
            final String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

            // List all queues.
            System.out.println("Listing all queues in your account.\n");
            for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();

            // Send a message.
            System.out.println("Sending a message to MyFifoQueue.fifo.\n");
            final SendMessageRequest sendMessageRequest =
                    new SendMessageRequest(myQueueUrl,
                            "This is my message text.");

            /*
             * When you send messages to a FIFO queue, you must provide a
             * non-empty MessageGroupId.
             */
            sendMessageRequest.setMessageGroupId("messageGroup1");

            // Uncomment the following to provide the MessageDeduplicationId
            //sendMessageRequest.setMessageDeduplicationId("1");
            final SendMessageResult sendMessageResult = sqs
                    .sendMessage(sendMessageRequest);
            final String sequenceNumber = sendMessageResult.getSequenceNumber();
            final String messageId = sendMessageResult.getMessageId();
            System.out.println("SendMessage succeed with messageId "
                    + messageId + ", sequence number " + sequenceNumber + "\n");

            ReceiveMessageUseCase rmuc = new ReceiveMessageUseCase(sqs, myQueueUrl);
            rmuc.handle();

//            // Delete the message.
//            System.out.println("Deleting the message.\n");
//            final String messageReceiptHandle = messages.get(0).getReceiptHandle();
//            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl,
//                    messageReceiptHandle));
//
//            // Delete the queue.
//            System.out.println("Deleting the queue.\n");
//            sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}