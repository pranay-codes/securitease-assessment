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
        when(customerRepository.findAll()).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery(null);

        assertThat(result).containsExactly(johnDoe);
        verify(customerRepository).findAll();
    }

    @Test
    void findCustomersByQueryReturnsAllCustomersWhenQueryIsBlank() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.findAll()).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery("   ");

        assertThat(result).containsExactly(johnDoe);
        verify(customerRepository).findAll();
    }

    @Test
    void findCustomersByQueryMatchesSubstringCaseInsensitivelyWithinAWord() {
        Customer annMarie = customer(1L, "Ann Marie");
        when(customerRepository.findByNameContainingIgnoreCase("ANN")).thenReturn(List.of(annMarie));

        List<Customer> result = customerSearchService.findCustomersByQuery("ANN");

        assertThat(result).containsExactly(annMarie);
    }

    @Test
    void findCustomersByQueryTrimsWhitespaceBeforeMatching() {
        Customer annMarie = customer(1L, "Ann Marie");
        when(customerRepository.findByNameContainingIgnoreCase("ann")).thenReturn(List.of(annMarie));

        List<Customer> result = customerSearchService.findCustomersByQuery("  ann  ");

        assertThat(result).containsExactly(annMarie);
    }

    @Test
    void findCustomersByQueryMatchesSubstringInAnyWord() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.findByNameContainingIgnoreCase("do")).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery("do");

        assertThat(result).containsExactly(johnDoe);
    }

    @Test
    void findCustomersByQueryDoesNotMatchAcrossWords() {
        Customer johnDoe = customer(1L, "John Doe");
        when(customerRepository.findByNameContainingIgnoreCase("hn do")).thenReturn(List.of(johnDoe));

        List<Customer> result = customerSearchService.findCustomersByQuery("hn do");

        assertThat(result).isEmpty();
    }

    @Test
    void findCustomersByQueryFiltersRepositoryResultsThatOnlyMatchAcrossWords() {
        Customer johnDoe = customer(1L, "John Doe");
        Customer johnDonne = customer(2L, "John Donne");
        when(customerRepository.findByNameContainingIgnoreCase("hn do")).thenReturn(List.of(johnDoe, johnDonne));

        List<Customer> result = customerSearchService.findCustomersByQuery("hn do");

        assertThat(result).isEmpty();
    }

    @Test
    void findCustomersByQueryIgnoresCustomersWithNullOrBlankNames() {
        Customer validCustomer = customer(1L, "Ann Marie");
        Customer nullNameCustomer = customer(2L, null);
        Customer blankNameCustomer = customer(3L, "   ");
        when(customerRepository.findByNameContainingIgnoreCase("ann"))
                .thenReturn(List.of(validCustomer, nullNameCustomer, blankNameCustomer));

        List<Customer> result = customerSearchService.findCustomersByQuery("ann");

        assertThat(result).containsExactly(validCustomer);
    }

    private Customer customer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }
}
