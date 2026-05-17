package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductQueryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ComponentScan(basePackageClasses = OrderMapper.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ProductQueryService productQueryService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(11L);
        product.setDescription("Keyboard");
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productQueryService.findProductById(11L)).thenReturn(productDto(11L, "Keyboard", List.of()));

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                  "description": "Keyboard"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.description").value("Keyboard"))
                .andExpect(jsonPath("$.orderIds").isArray())
                .andExpect(jsonPath("$.orderIds").isEmpty());
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productQueryService.findProducts())
                .thenReturn(
                        List.of(productDto(11L, "Keyboard", List.of(1L, 2L)), productDto(12L, "Mouse", List.of(2L))));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].description").value("Keyboard"))
                .andExpect(jsonPath("$[0].orderIds[0]").value(1))
                .andExpect(jsonPath("$[0].orderIds[1]").value(2))
                .andExpect(jsonPath("$[1].id").value(12))
                .andExpect(jsonPath("$[1].description").value("Mouse"))
                .andExpect(jsonPath("$[1].orderIds[0]").value(2));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productQueryService.findProductById(11L)).thenReturn(productDto(11L, "Keyboard", List.of(1L, 2L)));

        mockMvc.perform(get("/products/11"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.description").value("Keyboard"))
                .andExpect(jsonPath("$.orderIds[0]").value(1))
                .andExpect(jsonPath("$.orderIds[1]").value(2));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productQueryService.findProductById(999L)).thenReturn(null);

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.path").value("/products/999"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void testGetProductByIdWithNonNumericId() throws Exception {
        mockMvc.perform(get("/products/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'id'"))
                .andExpect(jsonPath("$.path").value("/products/abc"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void testCreateProductReturnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request body"))
                .andExpect(jsonPath("$.path").value("/products"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    private ProductDTO productDto(Long id, String description, List<Long> orderIds) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setDescription(description);
        productDTO.setOrderIds(orderIds);
        return productDTO;
    }
}
