package com.hwans.screenshareserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ScreenShareServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScreenShareServerApplication.class, args);
    }

}
