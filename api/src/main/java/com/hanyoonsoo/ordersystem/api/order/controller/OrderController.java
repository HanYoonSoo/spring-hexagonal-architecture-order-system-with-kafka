package com.hanyoonsoo.ordersystem.api.order.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.order.dto.request.OrderRequest;
import com.hanyoonsoo.ordersystem.api.order.dto.response.OrderRequestResponse;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
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
    public ApiResponse<OrderRequestResponse> requestOrder(@Valid @RequestBody OrderRequest request) {
        UUID orderId = orderService.requestOrder(request.toCommand(SecurityUtils.requiredAuthenticatedUserId()));
        return ApiResponse.success(OrderRequestResponse.pending(orderId));
    }
}
