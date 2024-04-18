package org.example.elasticsearch.Controller;

import lombok.RequiredArgsConstructor;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.example.clients.Entities.Article;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Service.ElasticArticleService;

import org.example.elasticsearch.dto.BooleanSearchRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/elastic")
@RequiredArgsConstructor
public class ElasticArticleController {
    private final ElasticArticleService elasticArticleService;

    @PostMapping("/save")
    public void saveArticles(@RequestBody List<Article> articles) {
        elasticArticleService.saveArticles(articles);
    }

    @PostMapping("/search")
    public Map<String, Object> dynamicSearch(@RequestBody BooleanSearchRequest searchParams) throws IOException {
        SearchResponse<ElasticArticle> searchResponse = elasticArticleService.dynamicBoolQueryImpl(searchParams);

        List<ElasticArticle> articles = new ArrayList<>();
        for (Hit<ElasticArticle> hit : searchResponse.hits().hits()) {
            articles.add(hit.source());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("articles", articles);
        response.put("totalHits", searchResponse.hits().total().value()); // Total hits metadata
        response.put("maxScore", searchResponse.hits().maxScore()); // Max score metadata
        return response;
    }
}
