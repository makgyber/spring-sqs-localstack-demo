package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FifoController {

    @GetMapping("/pis")
    public String index() throws InterruptedException {
        Thread.sleep(5000);
        return "Greetings from Spring Boot!";
    }
}
