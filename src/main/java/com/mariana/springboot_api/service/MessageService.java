package com.mariana.springboot_api.service;

import com.mariana.springboot_api.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public String getMessage(){
        return messageRepository.getMessage();
    }
}
