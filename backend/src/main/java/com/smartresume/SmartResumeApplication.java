package com.smartresume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartResumeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartResumeApplication.class, args);
    }
}
