package com.example.store.service;

import com.example.store.entity.Customer;
import com.example.store.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSearchServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerSearchService customerSearchService;

    @BeforeEach
    void setUp() {
        customerSearchService = new CustomerSearchService(customerRepository);
    }

    @Test
    void findCustomersByQueryReturnsAllCustomersWhenQueryIsNull() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.findAllByOrderByIdAsc()).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery(null);

        assertThat(result).containsExactly(johnDoe);
        verify(customerRepository).findAllByOrderByIdAsc();
    }

    @Test
    void findCustomersByQueryReturnsAllCustomersWhenQueryIsBlank() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.findAllByOrderByIdAsc()).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery("   ");

        assertThat(result).containsExactly(johnDoe);
        verify(customerRepository).findAllByOrderByIdAsc();
    }

    @Test
    void findCustomersByQueryMatchesSubstringCaseInsensitivelyWithinAWord() {
        Customer annMarie = customer(1L, "Ann Marie");
        when(customerRepository.searchByNameWordContaining("ANN")).thenReturn(List.of(annMarie));

        List<Customer> result = customerSearchService.findCustomersByQuery("ANN");

        assertThat(result).containsExactly(annMarie);
    }

    @Test
    void findCustomersByQueryTrimsWhitespaceBeforeMatching() {
        Customer annMarie = customer(1L, "Ann Marie");
        when(customerRepository.searchByNameWordContaining("ann")).thenReturn(List.of(annMarie));

        List<Customer> result = customerSearchService.findCustomersByQuery("  ann  ");

        assertThat(result).containsExactly(annMarie);
    }

    @Test
    void findCustomersByQueryMatchesSubstringInAnyWord() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.searchByNameWordContaining("do")).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery("do");

        assertThat(result).containsExactly(johnDoe);
    }

    @Test
    void findCustomersByQueryDoesNotMatchAcrossWords() {
        when(customerRepository.searchByNameWordContaining("hn do")).thenReturn(List.of());

        List<Customer> result = customerSearchService.findCustomersByQuery("hn do");

        assertThat(result).isEmpty();
    }

    @Test
    void findCustomersByQueryFiltersRepositoryResultsThatOnlyMatchAcrossWords() {
        when(customerRepository.searchByNameWordContaining("hn do")).thenReturn(List.of());

        List<Customer> result = customerSearchService.findCustomersByQuery("hn do");

        assertThat(result).isEmpty();
    }

    private Customer customer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }
}
