package com.example.store.service;

import com.example.store.entity.Customer;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerSearchService {

    private final CustomerRepository customerRepository;

    public List<Customer> findCustomersByQuery(String query) {
        if (query == null || query.isBlank()) {
            return customerRepository.findAllByOrderByIdAsc();
        }

        String normalizedQuery = query.trim();
        return customerRepository.searchByNameWordContaining(normalizedQuery);
    }
}
