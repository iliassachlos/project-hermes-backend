package org.example.clients.article;

import org.example.clients.article.dto.ArticlesResponse;
import org.example.clients.article.dto.FiltersRequest;
import org.example.clients.article.dto.ViewsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("article")
public interface ArticleClient {

    @GetMapping("api/articles/all")
    ArticlesResponse getAllArticles();

    @PutMapping("api/articles/{uuid}/views")
    ViewsResponse updateArticleViewCount(@PathVariable String uuid);

    @PostMapping("api/articles/filters")
    ArticlesResponse getArticlesByFilters(@RequestBody FiltersRequest filterRequest);
}
