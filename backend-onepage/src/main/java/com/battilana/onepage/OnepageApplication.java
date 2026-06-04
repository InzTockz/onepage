package com.battilana.onepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class OnepageApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnepageApplication.class, args);
    }

}
