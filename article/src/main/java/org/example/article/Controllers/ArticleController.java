package org.example.article.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.article.Services.ArticleService;
import org.example.clients.dto.article.ViewsResponse;
import org.example.clients.Entities.Article;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkArticleServiceStatus(){
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        return articleService.getArticleByUuid(id);
    }

    @PutMapping("/{id}/views")
    public ResponseEntity<ViewsResponse> updateArticleViewCount(@PathVariable String id) {
        return articleService.updateArticleViewCount(id);
    }

    @DeleteMapping("/{uuid}/delete")
    public ResponseEntity<String> deleteByUuid(@PathVariable String uuid) {
        return articleService.deleteArticleByUuid(uuid);
    }

}
