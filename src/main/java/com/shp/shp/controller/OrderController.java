package com.shp.shp.controller;


import com.shp.shp.dto.OrderDtos.OrderRequest;
import com.shp.shp.entity.*;
import com.shp.shp.repository.*;
import com.shp.shp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional // Important: Ensures Order is saved AND Cart is cleared in one go
    public Order placeOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();

        // 1. Initialize Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING"); // In real app, update this after payment gateway callback
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress("User Address ID: " + request.getAddressId());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // 2. Logic Split: From Cart vs. Buy Now
        if (request.isFromCart()) {
            // A. Fetch Cart
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Cart is empty"));

            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // B. Convert CartItems to OrderItems
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = createOrderItem(order, cartItem.getProduct(), cartItem.getQuantity());
                orderItems.add(orderItem);
                total = total.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }

            // C. Clear Cart after processing
            cartItemRepository.deleteByCartId(cart.getId());

        } else {
            // D. "Buy Now" (Direct items)
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new RuntimeException("No items provided");
            }

            for (var itemReq : request.getItems()) {
                Product p = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = createOrderItem(order, p, itemReq.getQuantity());
                orderItems.add(orderItem);
                total = total.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    // Helper method to create OrderItem
    private OrderItem createOrderItem(Order order, Product product, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        // Lock in the price at time of purchase
        item.setPriceAtPurchase(product.getSalePrice() != null ? product.getSalePrice() : product.getPrice());
        return item;
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<Order> getMyOrders(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();
        return orderRepository.findByUserId(user.getId());
    }
}