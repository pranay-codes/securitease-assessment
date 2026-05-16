package com.example.store.mapper;

import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderProductDTO;
import com.example.store.dto.OrderSummaryDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    default OrderDTO orderToOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setCustomer(orderToOrderCustomerDTO(order.getCustomer()));
        dto.setProducts(productsToOrderProductDTOs(order.getProducts()));
        return dto;
    }

    default List<OrderDTO> ordersToOrderDTOs(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::orderToOrderDTO).toList();
    }

    default OrderSummaryDTO orderToOrderSummaryDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setCustomer(orderToOrderCustomerDTO(order.getCustomer()));
        dto.setProducts(productsToOrderProductDTOs(order.getProducts()));
        return dto;
    }

    default List<OrderSummaryDTO> ordersToOrderSummaryDTOs(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::orderToOrderSummaryDTO).toList();
    }

    default OrderCustomerDTO orderToOrderCustomerDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        OrderCustomerDTO dto = new OrderCustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        return dto;
    }

    default List<OrderProductDTO> productsToOrderProductDTOs(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Long::compareTo)))
                .map(this::productToOrderProductDTO)
                .toList();
    }

    default OrderProductDTO productToOrderProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        OrderProductDTO dto = new OrderProductDTO();
        dto.setId(product.getId());
        dto.setDescription(product.getDescription());
        return dto;
    }

    default ProductDTO productToProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setDescription(product.getDescription());
        dto.setOrderIds(product.getOrders() == null
                ? Collections.emptyList()
                : product.getOrders().stream()
                        .filter(Objects::nonNull)
                        .map(Order::getId)
                        .filter(Objects::nonNull)
                        .sorted()
                        .toList());
        return dto;
    }

    default List<ProductDTO> productsToProductDTOs(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream().map(this::productToProductDTO).toList();
    }
}
