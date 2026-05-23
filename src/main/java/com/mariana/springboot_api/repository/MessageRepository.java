package com.mariana.springboot_api.repository;

import org.springframework.stereotype.Repository;

@Repository
public class MessageRepository {

    public String getMessage(){
        return "Hello from repository";
    }
}
