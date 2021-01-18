package com.zq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RocketMQSBApplication {

    public static void main(String[] args) {

        try {
            SpringApplication.run(RocketMQSBApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
