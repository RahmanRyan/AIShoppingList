package org.example.scraper.repository;

import org.example.scraper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;  // Add this import


public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
}