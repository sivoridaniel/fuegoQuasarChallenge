package com.challenge.meli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.challenge.meli")
public class QuasarApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuasarApplication.class, args);
    }

}

