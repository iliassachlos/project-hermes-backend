package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.scraping.Service.ScrapingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/scraping")
@RequiredArgsConstructor
public class ScrapingController {

    private final ScrapingService scrapingService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkScrapingServiceStatus() {
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PreProcessedArticle>> fetchArticles() {
        List<PreProcessedArticle> fetchedArticles = scrapingService.scrapeArticles();
        scrapingService.savePreProcessedArticles(fetchedArticles);
        scrapingService.getAllPreprocessedArticles();
        return ResponseEntity.status(HttpStatus.OK).body(fetchedArticles);
    }

    @PostMapping("/scrape/elastic")
    private ResponseEntity<String> saveToElastic() {
        return scrapingService.saveToElastic();
    }
}