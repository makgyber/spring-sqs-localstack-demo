package com.example.demo.useCase;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.partitions.model.Endpoint;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.demo.provider.FifoQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

//
//@RunWith(LocalstackDockerTestRunner.class)
//@LocalstackDockerProperties(randomizePorts = true, services = { "sqs" })
public class ConsumeQueueUseCaseTest {


    @Test
    public void handletest() {

        ConsumeQueueUseCase cq = new ConsumeQueueUseCase();
        cq.handle();
    }
}