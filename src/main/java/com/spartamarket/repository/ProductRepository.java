package com.spartamarket.repository;

import com.spartamarket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTitle(String title);
    Page<Product> findAll(Pageable pageable);
}
