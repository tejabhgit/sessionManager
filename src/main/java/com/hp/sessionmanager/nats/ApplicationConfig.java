package com.hp.sessionmanager.nats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class ApplicationConfig {

    @Bean
    public Consumer<Message<String>> notificationEventSupplier() {
        return message -> new EmailSender().sendEmail(message.getPayload());
    }
}
