package com.shp.shp.dto.OrderDtos;

import com.shp.shp.dto.OrderDtos.OrderItemRequest;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;

    // New flag: true if checking out the whole cart
    private boolean fromCart;

    // Optional: Only used if fromCart is false (Buy Now)
    private List<OrderItemRequest> items;
}