package com.mariana.springboot_api.repository;

import com.mariana.springboot_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository <Product, Long> {
}
