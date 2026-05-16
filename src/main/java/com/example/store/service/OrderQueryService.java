package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderSummaryDTO;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    static final int DEFAULT_PAGE_SIZE = 20;
    static final int MAX_PAGE_SIZE = 100;

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Page<OrderSummaryDTO> findOrders(Pageable pageable) {
        Pageable normalizedPageable = normalizePageable(pageable);
        return orderRepository.findAllWithCustomer(normalizedPageable).map(orderMapper::orderToOrderSummaryDTO);
    }

    public OrderDTO findOrderById(Long id) {
        return orderRepository
                .findDetailedById(id)
                .map(orderMapper::orderToOrderDTO)
                .orElse(null);
    }

    Pageable normalizePageable(Pageable pageable) {
        int pageNumber = pageable.isPaged() ? pageable.getPageNumber() : 0;
        int requestedSize = pageable.isPaged() ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        int normalizedSize = Math.min(Math.max(requestedSize, 1), MAX_PAGE_SIZE);
        return PageRequest.of(pageNumber, normalizedSize, pageable.getSort());
    }
}
