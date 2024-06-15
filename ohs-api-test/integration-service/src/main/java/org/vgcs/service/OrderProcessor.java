package org.vgcs.service;

import com.google.protobuf.StringValue;

import lombok.NonNull;
import order.Order;
import order.OrderServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vgcs.model.RowOrder;
import org.vgcs.model.ProcessedOrder;
import user.User;
import user.UserServiceGrpc;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderProcessor implements ItemProcessor<RowOrder, ProcessedOrder> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessor.class);

    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Autowired
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    @Override
    public ProcessedOrder process(@NonNull RowOrder newOrder) throws Exception {
        CompletableFuture<User.UserResponse> userFuture = getUserFuture(newOrder);

        User.UserResponse userResponse = userFuture.get();
        String userPid = userResponse.getPid();
        LOGGER.info("User created. userId: {}", userPid);

        CompletableFuture<Order.OrderResponse> orderFuture = getOrderFuture(newOrder, userPid);

        Order.OrderResponse orderResponse = orderFuture.get();
        String orderPid = orderResponse.getPid();
        String supplierPid = ""; // TODO: call supplier service to retrieve the supplier ID

        LOGGER.info("Order created. orderId: {}", orderPid);
        return new ProcessedOrder(userPid, orderPid, supplierPid);
    }

    private CompletableFuture<User.UserResponse> getUserFuture(RowOrder newOrder) {
        return CompletableFuture.supplyAsync(() -> {
            User.CreateUserRequest userRequest = User.CreateUserRequest.newBuilder()
                    .setFullName(StringValue.of(newOrder.getFullName()))
                    .setEmail(newOrder.getEmail())
                    .setAddress(User.ShippingAddress.newBuilder()
                            .setAddress(StringValue.of(newOrder.getShippingAddress()))
                            .setCountry(StringValue.of(newOrder.getCountry()))
                            .build())
                    .addPaymentMethods(User.PaymentMethod.newBuilder()
                            .setCreditCardNumber(StringValue.of(newOrder.getCreditCardNumber()))
                            .setCreditCardType(StringValue.of(newOrder.getCreditCardType()))
                            .build())
                    .build();
            return userServiceStub.createUser(userRequest);
        });
    }

    private CompletableFuture<Order.OrderResponse> getOrderFuture(RowOrder newOrder, String userPid) {
        return CompletableFuture.supplyAsync(() -> {
            Order.CreateOrderRequest orderRequest = Order.CreateOrderRequest.newBuilder()
                    .setUserPid(userPid)
                    .setDateCreated(StringValue.of(newOrder.getDateCreated().substring(0, 10)))
                    .setStatus(Order.OrderStatus.forNumber(newOrder.getOrderStatus()))
                    .addProducts(Order.Product.newBuilder()
                            .setPid(newOrder.getProductPid())
                            .setQuantity(newOrder.getQuantity())
                            .build())
                    .build();
            return orderServiceStub.createOrder(orderRequest);
        });
    }
}


