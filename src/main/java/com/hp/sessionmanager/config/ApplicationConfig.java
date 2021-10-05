
package com.hp.sessionmanager.config;

import com.example.grpc.server.grpcserver.APIResponse;
import com.example.grpc.server.grpcserver.TicketServiceGrpc;
import com.hp.sessionmanager.nats.TicketResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import java.util.function.Consumer;

@Configuration
public class ApplicationConfig  {

    @Bean
    public Consumer<Message<String>> notificationEventSupplier() {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        TicketServiceGrpc.TicketServiceBlockingStub stub = TicketServiceGrpc.newBlockingStub(channel);
        com.example.grpc.server.grpcserver.LoginRequest request =  com.example.grpc.server.grpcserver.LoginRequest.newBuilder().setUsername("Hello").setPassword("Hello").build();
       APIResponse response = stub.loginRequest(request);
        System.out.println("Response : "+ response.getResponseMessage());
        return message -> new TicketResponse().receiverResponse(response.getResponseMessage());
    }
}


