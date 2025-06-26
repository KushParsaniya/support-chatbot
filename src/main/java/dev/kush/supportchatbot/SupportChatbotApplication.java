package dev.kush.supportchatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SupportChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportChatbotApplication.class, args);
    }

}
