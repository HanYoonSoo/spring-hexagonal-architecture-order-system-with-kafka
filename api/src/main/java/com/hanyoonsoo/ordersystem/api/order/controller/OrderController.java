package com.hanyoonsoo.ordersystem.api.order.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.order.dto.request.CreateOrderRequest;
import com.hanyoonsoo.ordersystem.api.order.dto.response.CreateOrderResponse;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServicePort orderService;

    @PostMapping
    public ApiResponse<CreateOrderResponse> requestOrder(@Valid @RequestBody CreateOrderRequest request) {
        UUID orderId = orderService.requestOrder(request.toCommand(SecurityUtils.requiredAuthenticatedUserId()));
        return ApiResponse.success(CreateOrderResponse.from(orderId, OrderStatus.PENDING));
    }
}
