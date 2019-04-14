package com.example.demo.provider;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface QueueProvider {

    List<Message> receiveMessages();
    void deleteMessage(Message message);

}
