package org.example.article.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.article.Services.ArticleService;
import org.example.clients.dto.article.ViewsResponse;
import org.example.clients.Entities.Article;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Article>> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        return articleService.getArticleByUuid(id);
    }

    @PutMapping("/{id}/views")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ViewsResponse> updateArticleViewCount(@PathVariable String id) {
        return articleService.updateArticleViewCount(id);
    }

}
