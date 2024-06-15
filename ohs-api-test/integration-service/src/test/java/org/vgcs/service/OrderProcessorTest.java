package org.vgcs.service;

import order.Order;
import order.OrderServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vgcs.model.ProcessedOrder;
import org.vgcs.model.RowOrder;
import user.User;
import user.UserServiceGrpc;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    @Mock
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Mock
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    @InjectMocks
    private OrderProcessor orderProcessor;

    private RowOrder testRowOrder;

    @BeforeEach
    void setUp() {
        testRowOrder = new RowOrder();
        testRowOrder.setFullName("Test User");
        testRowOrder.setEmail("test@example.com");
        testRowOrder.setShippingAddress("123 Test Street");
        testRowOrder.setCountry("Testland");
        testRowOrder.setCreditCardNumber("1234567890123456");
        testRowOrder.setCreditCardType("VISA");
        testRowOrder.setDateCreated("2022-06-24T10:45:52Z");
        testRowOrder.setOrderStatus(1);
        testRowOrder.setProductPid("product123");
        testRowOrder.setQuantity(2);
    }

    @Test
    void testProcess() throws Exception {
        User.UserResponse userResponse = User.UserResponse.newBuilder().setPid("user123").build();
        Order.OrderResponse orderResponse = Order.OrderResponse.newBuilder().setPid("order123").build();

        when(userServiceStub.createUser(any(User.CreateUserRequest.class))).thenReturn(userResponse);
        when(orderServiceStub.createOrder(any(Order.CreateOrderRequest.class))).thenReturn(orderResponse);

        ProcessedOrder processedOrder = orderProcessor.process(testRowOrder);

        assertNotNull(processedOrder);
        assertEquals("user123", processedOrder.getUserPid());
        assertEquals("order123", processedOrder.getOrderPid());
        assertEquals("", processedOrder.getSupplierPid());
    }

    @Test
    void testGetUserFuture() throws Exception {
        User.UserResponse userResponse = User.UserResponse.newBuilder().setPid("user123").build();
        when(userServiceStub.createUser(any(User.CreateUserRequest.class))).thenReturn(userResponse);

        CompletableFuture<User.UserResponse> userFuture = orderProcessor.getUserFuture(testRowOrder);
        User.UserResponse result = userFuture.get();

        assertNotNull(result);
        assertEquals("user123", result.getPid());
    }

    @Test
    void testGetOrderFuture() throws Exception {
        Order.OrderResponse orderResponse = Order.OrderResponse.newBuilder().setPid("order123").build();
        when(orderServiceStub.createOrder(any(Order.CreateOrderRequest.class))).thenReturn(orderResponse);

        CompletableFuture<Order.OrderResponse> orderFuture = orderProcessor.getOrderFuture(testRowOrder, "user123");
        Order.OrderResponse result = orderFuture.get();

        assertNotNull(result);
        assertEquals("order123", result.getPid());
    }
}