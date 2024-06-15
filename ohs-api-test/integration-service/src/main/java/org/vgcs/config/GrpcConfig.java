package org.vgcs.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import order.OrderServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.UserServiceGrpc;

@Configuration
public class GrpcConfig {

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 50054)
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel orderServiceChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceStub(ManagedChannel userServiceChannel) {
        return UserServiceGrpc.newBlockingStub(userServiceChannel);
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub(ManagedChannel orderServiceChannel) {
        return OrderServiceGrpc.newBlockingStub(orderServiceChannel);
    }
}
