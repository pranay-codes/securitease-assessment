package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByOrderByIdAsc();

    @Query(
            value =
                    """
            SELECT c.*
            FROM customer c
            WHERE EXISTS (
                SELECT 1
                FROM regexp_split_to_table(lower(c.name), E'\\\\s+') AS word
                WHERE word LIKE CONCAT('%', lower(:query), '%')
            )
            ORDER BY c.id
            """,
            nativeQuery = true)
    List<Customer> searchByNameWordContaining(@Param("query") String query);
}
