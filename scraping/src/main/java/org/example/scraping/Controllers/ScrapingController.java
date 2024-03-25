package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.ArticleClient;
import org.example.clients.ArticlesResponse;
import org.example.scraping.Service.ScrapingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public ArticlesResponse fetchArticles() {
        ArticlesResponse articles = scrapingService.fetchArticlesFromWebsites();
        scrapingService.saveArticles(articles);
        return articles;
    }
}
