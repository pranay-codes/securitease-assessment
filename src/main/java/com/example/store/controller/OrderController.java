package com.example.store.controller;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderSummaryDTO;
import com.example.store.entity.Order;
import com.example.store.repository.OrderRepository;
import com.example.store.service.OrderQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;

    @GetMapping
    public Page<OrderSummaryDTO> getAllOrders(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return orderQueryService.findOrders(pageable);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        OrderDTO order = orderQueryService.findOrderById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);
        return orderQueryService.findOrderById(savedOrder.getId());
    }
}
