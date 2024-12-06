package org.example.scraper.controller;

import org.example.scraper.service.ChatGPTService;
import org.example.scraper.service.WebScraper;
import org.example.scraper.model.Product;
import org.example.scraper.repository.ProductRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {
    private static final Logger log = LoggerFactory.getLogger(ScraperController.class);

    @Autowired private WebScraper webScraper;
    @Autowired private ChatGPTService chatGPTService;
    @Autowired private ProductRepository productRepository;

    @GetMapping("/extract/gpt")
    public String extractProductInfoGPT(@RequestParam String url) {
        try {
            String html = webScraper.scrapeWebpage(url);
            log.info("Scraped HTML length: {}", html.length());
            String result = chatGPTService.extractProductInfo(html);

            // Parse the response and save to MongoDB
            JSONObject json = new JSONObject(result);
            Product product = new Product(
                    json.getString("name"),
                    json.getString("price"),
                    url
            );
            productRepository.save(product);

            return result;
        } catch (Exception e) {
            log.error("Failed to process URL with ChatGPT", e);
            return "{\"error\": \"Failed to process URL with ChatGPT\"}";
        }
    }

    @GetMapping("/products")
    public Iterable<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return ResponseEntity.ok().body("{\"message\": \"Product deleted successfully\"}");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting product with id: " + id, e);
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to delete product\"}");
        }
    }
}