package org.vgcs.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import order.OrderServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.UserServiceGrpc;

@Configuration
public class GrpcConfig {
    @Value("${service.url}")
    private String url;

    @Value("${user.service.port}")
    private Integer userPort;

    @Value("${order.service.port}")
    private Integer orderPort;

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forAddress(url, userPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel orderServiceChannel() {
        return ManagedChannelBuilder.forAddress(url, orderPort)
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
