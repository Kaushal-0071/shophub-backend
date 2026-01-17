package com.shp.shp.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private BigDecimal totalAmount;
    private List<CartItemDto> items;
}

