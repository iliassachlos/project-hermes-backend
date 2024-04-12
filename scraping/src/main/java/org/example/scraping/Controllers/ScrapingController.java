package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.ElasticsearchClient;
import org.example.clients.Entities.Article;
import org.example.scraping.Service.ScrapingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/scraping")
@RequiredArgsConstructor
public class ScrapingController {

    private final ScrapingService scrapingService;
    private final ElasticsearchClient elasticsearchClient;

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> fetchArticles() {
        List<Article> articles = scrapingService.fetchArticlesFromWebsites();
        scrapingService.saveArticles(articles);
        scrapingService.deleteOldArticles();
        return articles;
    }
}