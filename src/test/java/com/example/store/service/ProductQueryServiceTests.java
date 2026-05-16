package com.example.store.service;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapperImpl;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTests {

    @Mock
    private ProductRepository productRepository;

    private ProductQueryService productQueryService;

    @BeforeEach
    void setUp() {
        productQueryService = new ProductQueryService(productRepository, new OrderMapperImpl());
    }

    @Test
    void findProductsReturnsProductsWithSortedOrderIds() {
        when(productRepository.findAllWithOrdersOrderByIdAsc())
                .thenReturn(List.of(product(11L, "Keyboard", List.of(order(2L), order(1L)))));

        List<ProductDTO> result = productQueryService.findProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Keyboard");
        assertThat(result.get(0).getOrderIds()).containsExactly(1L, 2L);
    }

    @Test
    void findProductByIdReturnsNullWhenMissing() {
        when(productRepository.findDetailedById(99L)).thenReturn(Optional.empty());

        ProductDTO result = productQueryService.findProductById(99L);

        assertThat(result).isNull();
    }

    private Product product(Long id, String description, List<Order> orders) {
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        product.setOrders(orders);
        return product;
    }

    private Order order(Long id) {
        Order order = new Order();
        order.setId(id);
        return order;
    }
}
