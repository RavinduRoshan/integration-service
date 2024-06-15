package org.vgcs.service;

import com.google.protobuf.StringValue;

import order.Order;
import order.OrderServiceGrpc;
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

    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Autowired
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;


    @Override
    public ProcessedOrder process(RowOrder newOrder) throws Exception {
        System.out.println("OOOOOOOOOOOOOOOOrder : " + newOrder.getFullName());
        CompletableFuture<User.UserResponse> userFuture = CompletableFuture.supplyAsync(() -> {
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

        User.UserResponse userResponse = userFuture.get();
        String userPid = userResponse.getPid();

        CompletableFuture<Order.OrderResponse> orderFuture = CompletableFuture.supplyAsync(() -> {
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

        Order.OrderResponse orderResponse = orderFuture.get();
        String orderPid = orderResponse.getPid();
        String supplierPid = "";

        return new ProcessedOrder(userPid, orderPid, supplierPid);
    }
}


