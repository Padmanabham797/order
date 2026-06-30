package com.example.controller;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.service.OrderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService service;

    public OrderController(
            OrderService service) {

        this.service = service;
    }

    @PostMapping
    public Order placeOrder(
            @RequestBody OrderRequest request) {

        return service.placeOrder(
                request.getProductId(),
                request.getQuantity());
    }

    @GetMapping
    public List<Order> getAllOrders() {

        return service.getAllOrders();
    }



 
}
