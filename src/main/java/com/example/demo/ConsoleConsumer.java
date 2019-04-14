package com.example.demo;


import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.util.Base64;
import com.example.demo.useCase.ConsumeQueueUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ConsoleConsumer
        implements CommandLineRunner {

    private static Logger LOG = LoggerFactory
            .getLogger(ConsoleConsumer.class);

    public static void main(String[] args) throws JMSException, InterruptedException{
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ConsoleConsumer.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) throws JMSException {
        LOG.info("EXECUTING : command line runner");
        String queueUrl = "http://localhost:4576/queue/payment_instructions.fifo";

        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration("http://localhost:4576", "REGION")
                        )

        );

        // Create the connection
        SQSConnection connection = connectionFactory.createConnection();

        // Create the session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer( session.createQueue( "payment_instructions.fifo" ) );

        connection.start();

        receiveMessages(session, consumer);

        // Close the connection. This closes the session automatically
        connection.close();
        System.out.println( "Connection closed" );
    }

    private void receiveMessages( Session session, MessageConsumer consumer ) throws JMSException{
        try {
            while( true ) {
                Message message = consumer.receive(3000);
                if( message == null ) {
                    System.out.println( "Waiting for messages");
//                    break;
                } else {
                    handleMessage(message);
//                    message.acknowledge();

                }
            }
        } catch (JMSException e) {
            System.err.println( "Error receiving from SQS: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) throws JMSException {
        try {
            System.out.println( "Got message " + message.getJMSMessageID() );
            final String uri = "http://localhost:8080/pis";

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            System.out.println(result);

            System.out.println( "Content: ");
            if( message instanceof TextMessage) {
                TextMessage txtMessage = ( TextMessage ) message;
                System.out.println( "\t" + txtMessage.getText() );
            } else if( message instanceof BytesMessage ){
                BytesMessage byteMessage = ( BytesMessage ) message;
                // Assume the length fits in an int - SQS only supports sizes up to 256k so that
                // should be true
                byte[] bytes = new byte[(int)byteMessage.getBodyLength()];
                byteMessage.readBytes(bytes);
                System.out.println( "\t" +  Base64.encodeAsString( bytes ) );
            } else if( message instanceof ObjectMessage ) {
                ObjectMessage objMessage = (ObjectMessage) message;
                System.out.println( "\t" + objMessage.getObject() );
            }
            message.acknowledge();
            System.out.println( "Acknowledged message " + message.getJMSMessageID() );
        } catch (JMSException e) {
            System.err.println( "Error receiving from SQS: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}