package com.example.service;

import com.example.dto.ProductResponse;
import com.example.dto.StockUpdateRequest;
import com.example.entity.Order;
import com.example.exception.InsufficientStockException;
import com.example.exception.ProductNotFoundException;
import com.example.feign.ProductClient;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.util.List;

@Service
public class OrderServiceImpl
        implements OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;

    public OrderServiceImpl(
            OrderRepository repository,
            ProductClient productClient) {

        this.repository = repository;
        this.productClient = productClient;
    }

    @Override
    @RateLimiter(name = "productService")
    @Retry(name = "productService")
    @CircuitBreaker(name = "productService", fallbackMethod = "orderFallback")
    public Order placeOrder(Long productId, Integer quantity) {

        ProductResponse product = productClient.getProduct(productId);

        if (product == null) {
            throw new ProductNotFoundException(
                    "Product Not Found");
        }

        if (quantity > product.getQuantity()) {
            throw new InsufficientStockException("Insufficient Stock");
        }

        Double totalPrice = product.getPrice() * quantity;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);

        Order savedOrder = repository.save(order);

        // Update Product Stock
        int remainingStock = product.getQuantity() - quantity;

        StockUpdateRequest request = new StockUpdateRequest();
        request.setQuantity(remainingStock);

        productClient.updateStock(productId, request);

        return savedOrder;
    }
    public Order orderFallback(Long productId,
                               Integer quantity,
                               Exception ex) {

        throw new RuntimeException(
                "Product Service is currently unavailable. Please try again later.");
    }
    @Override
    public List<Order> getAllOrders() {

        return repository.findAll();
    }

}
