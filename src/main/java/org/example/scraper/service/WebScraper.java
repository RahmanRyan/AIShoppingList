package org.example.scraper.service;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class WebScraper {
    public String scrapeWebpage(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124")
                .header("Accept-Language", "en-US,en;q=0.9")
                .get()
                .text(); // Changed from .html() to .text()
    }
}