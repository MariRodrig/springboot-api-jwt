package com.mariana.springboot_api.controller;

import com.mariana.springboot_api.exceptions.ResourceNotFoundException;
import com.mariana.springboot_api.model.Product;
import com.mariana.springboot_api.security.JwtAuthFilter;
import com.mariana.springboot_api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnProducts() throws Exception {

        Product product = new Product("Notebook", 3500.0);

        when(productService.listProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Notebook"))
                .andExpect(jsonPath("$[0].price").value(3500.0));

        verify(productService).listProducts();
    }

    @Test
    void shouldReturnProductById() throws Exception {

        Product product = new Product("Notebook", 3500.0);

        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(3500.0));

        verify(productService).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

        when(productService.findById(999L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found")).andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService).findById(999L);
    }

    @Test
    void shouldCreateProduct() throws Exception {

        Product product = new Product("Notebook", 3500.0);

        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(3500.0));

        verify(productService).saveProduct(any(Product.class));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productService).saveProduct(captor.capture());

        Product capturedProduct = captor.getValue();

        assertEquals("Notebook", capturedProduct.getName());
        assertEquals(3500.0, capturedProduct.getPrice());
    }

    @Test
    void shouldDeleteProduct() throws Exception {

        mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingProduct() throws Exception {

        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService).deleteProduct(999L);
    }
}
