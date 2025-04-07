package com.datn.electronic_voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
        (exclude = {SecurityAutoConfiguration.class })
public class ElectronicVotingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectronicVotingApplication.class, args);
    }

}
