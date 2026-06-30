package com.example.service;

import com.example.entity.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(
            Long productId,
            Integer quantity);

    List<Order> getAllOrders();
}