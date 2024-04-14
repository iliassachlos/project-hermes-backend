package org.example.clients;

import org.example.clients.Entities.Article;
import org.example.clients.dto.article.ArticlesResponse;
import org.example.clients.dto.article.FiltersRequest;
import org.example.clients.dto.article.ViewsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("article")
public interface ArticleClient {

    @GetMapping("api/articles/all")
    ArticlesResponse getAllArticles();

    @GetMapping("api/articles/{id}")
    Article getArticleById(@PathVariable String id);

    @PutMapping("api/articles/{id}/views")
    ViewsResponse updateArticleViewCount(@PathVariable String id);

    @PostMapping("api/articles/filters")
    ArticlesResponse getArticlesByFilters(@RequestBody FiltersRequest filterRequest);
}
