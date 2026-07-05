package com.roadcard.dockwebhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DockWebhookApplication {
    public static void main(String[] args) {
        SpringApplication.run(DockWebhookApplication.class, args);
    }
}
