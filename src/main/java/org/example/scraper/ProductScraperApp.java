package org.example.scraper;  // Base package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.example.scraper",
        "org.example.scraper.config",
        "org.example.scraper.service",
        "org.example.scraper.controller"
})
public class ProductScraperApp {
    public static void main(String[] args) {
        SpringApplication.run(ProductScraperApp.class, args);
    }
}