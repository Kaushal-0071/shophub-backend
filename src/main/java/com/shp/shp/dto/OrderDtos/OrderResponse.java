package com.shp.shp.dto.OrderDtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private List<OrderItemResponse> items;
}