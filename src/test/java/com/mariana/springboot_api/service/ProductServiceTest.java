package com.mariana.springboot_api.service;


import com.mariana.springboot_api.exceptions.ResourceNotFoundException;
import com.mariana.springboot_api.model.Product;
import com.mariana.springboot_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnProductWhenIdExits() {
        Long id = 1L;
        Product product = new Product("Notebook", 3000.0);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.findById(id);
        assertSame(product, result);
    }

    @Test
    void shouldThrowExceptionWhenIdDoesNotExist() {
        Long id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(id));
    }

    @Test
    void shouldDeleteProductWhenIdExists() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(true);
        productService.deleteProduct(id);
        verify(productRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentProduct() {
        Long id = 99L;

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(id));
        verify(productRepository, never()).deleteById(id);
    }

    @Test
    void shouldSaveProduct() {

        Product product = new Product();

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertSame(product, result);

        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnAllProducts() {
        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> products = List.of(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.listProducts();

        assertEquals(2, result.size());
        assertSame(products, result);

        verify(productRepository).findAll();
    }

}
