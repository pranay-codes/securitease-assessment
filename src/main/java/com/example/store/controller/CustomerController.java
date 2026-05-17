package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.CustomerSummaryDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.CustomerSearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerSearchService customerSearchService;

    @GetMapping
    public ResponseEntity<List<CustomerSummaryDTO>> getAllCustomers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(
                customerMapper.customersToCustomerSummaryDTOs(customerSearchService.findCustomersByQuery(query)));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerMapper.customerToCustomerDTO(customerRepository.save(customer)));
    }
}
