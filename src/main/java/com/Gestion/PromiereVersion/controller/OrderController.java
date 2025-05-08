package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.OrderResponseDTO;
import com.Gestion.PromiereVersion.model.Order;
import com.Gestion.PromiereVersion.service.OrderService;
import com.Gestion.PromiereVersion.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderMapper.toOrderResponseDTO(order));
    }
} 