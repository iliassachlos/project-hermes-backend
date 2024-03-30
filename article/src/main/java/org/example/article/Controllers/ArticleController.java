package org.example.article.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.article.Services.ArticleService;
import org.example.clients.article.dto.ArticlesResponse;
import org.example.clients.article.dto.FiltersRequest;
import org.example.clients.article.dto.ViewsResponse;
import org.example.clients.article.Entities.Article;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public void getAllArticles() {
         articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Article getArticleById(@PathVariable String id) {
        return articleService.getArticleById(id);
    }

    @PutMapping("/{id}/views")
    @ResponseStatus(HttpStatus.OK)
    public ViewsResponse updateArticleViewCount(@PathVariable String id) {
        return articleService.updateArticleViewCount(id);
    }

    @PostMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public ArticlesResponse getArticlesByFilters(@RequestBody FiltersRequest filterRequest) {
        return articleService.getArticlesByFilters(filterRequest.categories());
    }
}
