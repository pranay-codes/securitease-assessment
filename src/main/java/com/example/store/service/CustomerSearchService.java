package com.example.store.service;

import com.example.store.entity.Customer;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomerSearchService {

    private final CustomerRepository customerRepository;

    public List<Customer> findCustomersByQuery(String query) {
        if (query == null || query.isBlank()) {
            return customerRepository.findAll();
        }

        String normalizedQuery = query.trim();
        return customerRepository.findByNameContainingIgnoreCase(normalizedQuery).stream()
                .filter(customer -> nameContainsQueryWithinSingleWord(customer.getName(), normalizedQuery))
                .toList();
    }

    private boolean nameContainsQueryWithinSingleWord(String name, String query) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        return List.of(name.split("\\s+")).stream()
                .map(word -> word.toLowerCase(Locale.ROOT))
                .anyMatch(word -> word.contains(normalizedQuery));
    }
}
