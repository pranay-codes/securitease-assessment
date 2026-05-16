package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "orders")
    @Query("select distinct p from Product p order by p.id")
    List<Product> findAllWithOrdersOrderByIdAsc();

    @EntityGraph(attributePaths = "orders")
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findDetailedById(@Param("id") Long id);
}
