package org.example.scraper.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import org.example.scraper.exception.AuthenticationException;
import org.example.scraper.service.ChatGPTService;
import org.example.scraper.service.WebScraper;
import org.example.scraper.model.Product;
import org.example.scraper.repository.ProductRepository;
import org.example.scraper.util.AuthUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {
    private static final Logger log = LoggerFactory.getLogger(ScraperController.class);

    @Autowired
    private WebScraper webScraper;
    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AuthUtil authUtil;  // Add this line

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            String userId = authUtil.verifyToken(token);
            return ResponseEntity.ok().body(Map.of("userId", userId));
        } catch (AuthenticationException e) {
            log.error("Invalid token", e);
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    @GetMapping("/extract/gpt")
    public ResponseEntity<?> extractProductInfoGPT(
            @RequestHeader("Authorization") String token,
            @RequestParam String url) {
        try {
            String userId = authUtil.verifyToken(token);

            String html = webScraper.scrapeWebpage(url);
            log.info("Scraped HTML length: {}", html.length());
            String result = chatGPTService.extractProductInfo(html);

            JSONObject json = new JSONObject(result);
            Product product = new Product(
                    userId,
                    json.getString("name"),
                    json.getString("price"),
                    url
            );
            productRepository.save(product);

            return ResponseEntity.ok(result);
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(401).body("{\"error\": \"Authentication failed\"}");
        } catch (Exception e) {
            log.error("Failed to process URL with ChatGPT", e);
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to process URL with ChatGPT\"}");
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        try {
            String userId = authUtil.verifyToken(token);
            log.info("Fetching products for user: {}", userId);
            return ResponseEntity.ok(productRepository.findByUserId(userId));
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(401).body("{\"error\": \"Authentication failed\"}");
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        try {
            String userId = authUtil.verifyToken(token);

            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }

            if (!product.getUserId().equals(userId)) {
                return ResponseEntity.status(403).body("{\"error\": \"Not authorized to delete this product\"}");
            }

            productRepository.deleteById(id);
            return ResponseEntity.ok().body("{\"message\": \"Product deleted successfully\"}");
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(401).body("{\"error\": \"Authentication failed\"}");
        } catch (Exception e) {
            log.error("Error deleting product with id: " + id, e);
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to delete product\"}");
        }
    }
}