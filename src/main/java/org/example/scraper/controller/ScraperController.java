package org.example.scraper.controller;

import org.example.scraper.service.ChatGPTService;
import org.example.scraper.service.WebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {
    private static final Logger log = LoggerFactory.getLogger(ScraperController.class);
    @Autowired private WebScraper webScraper;
//    @Autowired private LocalLLMService localLLMService;
    @Autowired private ChatGPTService chatGPTService;

//    @GetMapping("/extract")
//    public String extractProductInfo(@RequestParam String url) {
//        try {
//            String html = webScraper.scrapeWebpage(url);
//            log.info(html);
//            return localLLMService.extractProductInfo(html);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "{\"error\": \"Failed to process URL\"}";
//        }
//    }

    @GetMapping("/extract/gpt")
    public String extractProductInfoGPT(@RequestParam String url) {
        try {
            String html = webScraper.scrapeWebpage(url);
            log.info("Scraped HTML length: {}", html.length());
            return chatGPTService.extractProductInfo(html);
        } catch (Exception e) {
            log.error("Failed to process URL with ChatGPT", e);
            return "{\"error\": \"Failed to process URL with ChatGPT\"}";
        }
    }
}