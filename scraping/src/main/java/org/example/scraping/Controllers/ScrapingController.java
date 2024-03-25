package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Article;
import org.example.clients.ArticleClient;
import org.example.clients.ArticlesResponse;
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
    public final ArticleClient articleClient;

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> fetchArticles() {
        List<Article> articles = scrapingService.fetchArticlesFromWebsites();
        scrapingService.saveArticles(articles);
        return articles;
    }
}
