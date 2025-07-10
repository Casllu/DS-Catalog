package com.lucasalmeida.dscatalog.repository;

import com.lucasalmeida.dscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
