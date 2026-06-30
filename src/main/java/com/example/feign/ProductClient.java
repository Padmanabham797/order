package com.example.feign;

import com.example.dto.ProductResponse;
import com.example.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProduct(
            @PathVariable Long id);
    @PutMapping("/api/products/{id}/stock")
    void updateStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request);
}