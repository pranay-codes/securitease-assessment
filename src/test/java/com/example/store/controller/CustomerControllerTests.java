package com.example.store.controller;

import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.CustomerSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ComponentScan(basePackageClasses = CustomerMapper.class)
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private CustomerSearchService customerSearchService;

    private Customer customer;
    private Customer secondCustomer;
    private Customer thirdCustomer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setId(1L);

        secondCustomer = new Customer();
        secondCustomer.setName("Alice Smith");
        secondCustomer.setId(2L);

        thirdCustomer = new Customer();
        thirdCustomer.setName("Joanna Stone");
        thirdCustomer.setId(3L);
    }

    @Test
    void testCreateCustomer() throws Exception {
        when(customerRepository.save(customer)).thenReturn(customer);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        when(customerSearchService.findCustomersByQuery(null)).thenReturn(List.of(customer));

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name").value("John Doe"))
                .andExpect(jsonPath("$[0].orders").doesNotExist());
    }

    @Test
    void testGetAllCustomersWhenEmpty() throws Exception {
        when(customerSearchService.findCustomersByQuery(null)).thenReturn(List.of());

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetCustomersByQueryReturnsMatchingCustomers() throws Exception {
        when(customerSearchService.findCustomersByQuery("oh")).thenReturn(List.of(customer));

        mockMvc.perform(get("/customer").param("query", "oh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].orders").doesNotExist());
    }

    @Test
    void testGetCustomersByQueryReturnsEmptyListWhenNoMatch() throws Exception {
        when(customerSearchService.findCustomersByQuery("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/customer").param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetCustomersByQueryMatchesSecondWord() throws Exception {
        when(customerSearchService.findCustomersByQuery("do")).thenReturn(List.of(customer));

        mockMvc.perform(get("/customer").param("query", "do"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetCustomersByQueryMatchesCaseInsensitively() throws Exception {
        when(customerSearchService.findCustomersByQuery("OH")).thenReturn(List.of(customer));

        mockMvc.perform(get("/customer").param("query", "OH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetCustomersByQueryReturnsMultipleMatches() throws Exception {
        when(customerSearchService.findCustomersByQuery("jo")).thenReturn(List.of(customer, thirdCustomer));

        mockMvc.perform(get("/customer").param("query", "jo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Joanna Stone"));
    }

    @Test
    void testGetCustomersWithBlankQueryReturnsAllCustomers() throws Exception {
        when(customerSearchService.findCustomersByQuery("")).thenReturn(List.of(customer, secondCustomer));

        mockMvc.perform(get("/customer").param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Alice Smith"));
    }

    @Test
    void testGetCustomersWithWhitespaceOnlyQueryReturnsAllCustomers() throws Exception {
        when(customerSearchService.findCustomersByQuery("   ")).thenReturn(List.of(customer, secondCustomer));

        mockMvc.perform(get("/customer").param("query", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Alice Smith"));
    }

    @Test
    void testCreateCustomerReturnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request body"))
                .andExpect(jsonPath("$.path").value("/customer"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
