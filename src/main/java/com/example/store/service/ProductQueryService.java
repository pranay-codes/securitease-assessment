package com.example.store.service;

import com.example.store.dto.ProductDTO;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public List<ProductDTO> findProducts() {
        return orderMapper.productsToProductDTOs(productRepository.findAllWithOrdersOrderByIdAsc());
    }

    public ProductDTO findProductById(Long id) {
        return productRepository
                .findDetailedById(id)
                .map(orderMapper::productToProductDTO)
                .orElse(null);
    }
}
