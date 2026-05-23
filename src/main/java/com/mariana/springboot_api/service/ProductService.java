package com.mariana.springboot_api.service;

import com.mariana.springboot_api.exceptions.ResourceNotFoundException;
import com.mariana.springboot_api.model.Product;
import com.mariana.springboot_api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product by id " +id+ " not found"));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product by id" +id+ "not found");
        }
        productRepository.deleteById(id);
    }
}

