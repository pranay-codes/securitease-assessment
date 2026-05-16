package com.example.store.service;

import com.example.store.dto.OrderSummaryDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapperImpl;
import com.example.store.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTests {

    @Mock
    private OrderRepository orderRepository;

    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        orderQueryService = new OrderQueryService(orderRepository, new OrderMapperImpl());
    }

    @Test
    void findOrdersCapsRequestedPageSizeAtMaximum() {
        Order order = order(1L, "Test Order", customer(1L, "John Doe"));
        when(orderRepository.findAllWithCustomer(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 100, Sort.by("id")), 1));

        Page<OrderSummaryDTO> page = orderQueryService.findOrders(PageRequest.of(0, 500, Sort.by("id")));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getSize()).isEqualTo(100);
        assertThat(page.getContent().get(0).getProducts())
                .extracting(com.example.store.dto.OrderProductDTO::getDescription)
                .containsExactly("Keyboard");
    }

    @Test
    void normalizePageableUsesAtLeastOneRecord() {
        Pageable pageable = orderQueryService.normalizePageable(PageRequest.of(0, 1, Sort.by("id")));

        assertThat(pageable.getPageSize()).isEqualTo(1);
    }

    private Order order(Long id, String description, Customer customer) {
        Order order = new Order();
        order.setId(id);
        order.setDescription(description);
        order.setCustomer(customer);
        order.setProducts(List.of(product(11L, "Keyboard")));
        return order;
    }

    private Customer customer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }

    private Product product(Long id, String description) {
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        return product;
    }
}
