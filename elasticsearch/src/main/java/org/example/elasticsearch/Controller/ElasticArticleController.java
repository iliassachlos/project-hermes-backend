package org.example.elasticsearch.Controller;

import lombok.RequiredArgsConstructor;
import org.example.clients.Entities.Article;
import org.example.elasticsearch.Service.ElasticArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/elastic")
@RequiredArgsConstructor
public class ElasticArticleController {
    private final ElasticArticleService articleService;

    @PostMapping("/save")
    public void saveArticles(@RequestBody List<Article> articles) {
        articleService.saveArticles(articles);
    }
}
