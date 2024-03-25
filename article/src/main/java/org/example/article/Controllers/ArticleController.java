package org.example.article.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.article.Services.ArticleService;
import org.example.clients.ArticlesResponse;
import org.example.clients.FiltersRequest;
import org.example.clients.ViewsResponse;
import org.example.clients.Article;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Article getArticleByUuid(@PathVariable String uuid) {
        return articleService.getArticleByUuid(uuid);
    }

    @PutMapping("/{uuid}/views")
    @ResponseStatus(HttpStatus.OK)
    public ViewsResponse updateArticleViewCount(@PathVariable String uuid) {
        return articleService.updateArticleViewCount(uuid);
    }

    @PostMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public ArticlesResponse getArticlesByFilters(@RequestBody FiltersRequest filterRequest) {
        return articleService.getArticlesByFilters(filterRequest.categories());
    }
}
