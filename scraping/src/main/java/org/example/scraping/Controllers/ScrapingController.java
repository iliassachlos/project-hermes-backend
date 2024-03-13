package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Article;
import org.example.scraping.Service.ScrapingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ScrapingController {
    private final ScrapingService scrapingService;

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> fetchArticles() {
        List<Article> articles = scrapingService.fetchArticlesFromWebsites();
        try {
            scrapingService.saveArticles(articles);
        } catch (Exception e) {
            log.error("Error occurred while fetching articles", e);
        } finally {
            scrapingService.deleteOldArticles();
        }
        return articles;
    }
}
