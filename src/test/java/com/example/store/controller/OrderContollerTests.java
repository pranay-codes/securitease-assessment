package com.example.store.controller;

import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.service.OrderQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ComponentScan(basePackageClasses = CustomerMapper.class)
@RequiredArgsConstructor
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private OrderQueryService orderQueryService;

    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setId(1L);

        order = new Order();
        order.setDescription("Test Order");
        order.setId(1L);
        order.setCustomer(customer);
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderRepository.save(order)).thenReturn(order);
        when(orderQueryService.findOrderById(1L)).thenReturn(orderDto());

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"));
    }

    @Test
    void testGetOrder() throws Exception {
        when(orderQueryService.findOrders(PageRequest.of(
                        0, 20, org.springframework.data.domain.Sort.by("id").ascending())))
                .thenReturn(new PageImpl<>(
                        java.util.List.of(orderSummaryDto()),
                        PageRequest.of(
                                0,
                                20,
                                org.springframework.data.domain.Sort.by("id").ascending()),
                        1));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Test Order"))
                .andExpect(jsonPath("$.content[0].customer.name").value("John Doe"))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetOrderWhenEmpty() throws Exception {
        when(orderQueryService.findOrders(PageRequest.of(
                        0, 20, org.springframework.data.domain.Sort.by("id").ascending())))
                .thenReturn(new PageImpl<>(
                        java.util.List.of(),
                        PageRequest.of(
                                0,
                                20,
                                org.springframework.data.domain.Sort.by("id").ascending()),
                        0));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderQueryService.findOrderById(1L)).thenReturn(orderDto());

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.customer.name").value("John Doe"));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        when(orderQueryService.findOrderById(999999L)).thenReturn(null);

        mockMvc.perform(get("/order/999999")).andExpect(status().isNotFound());
    }

    @Test
    void testGetOrderByIdWithNonNumericId() throws Exception {
        mockMvc.perform(get("/order/abc")).andExpect(status().isBadRequest());
    }

    private com.example.store.dto.OrderDTO orderDto() {
        com.example.store.dto.OrderCustomerDTO orderCustomerDTO = new com.example.store.dto.OrderCustomerDTO();
        orderCustomerDTO.setId(1L);
        orderCustomerDTO.setName("John Doe");

        com.example.store.dto.OrderDTO orderDTO = new com.example.store.dto.OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setDescription("Test Order");
        orderDTO.setCustomer(orderCustomerDTO);
        return orderDTO;
    }

    private com.example.store.dto.OrderSummaryDTO orderSummaryDto() {
        com.example.store.dto.OrderSummaryDTO orderSummaryDTO = new com.example.store.dto.OrderSummaryDTO();
        orderSummaryDTO.setId(1L);
        orderSummaryDTO.setDescription("Test Order");
        orderSummaryDTO.setCustomer(orderDto().getCustomer());
        return orderSummaryDTO;
    }
}
