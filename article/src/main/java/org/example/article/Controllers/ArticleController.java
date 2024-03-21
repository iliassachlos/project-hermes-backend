package org.example.article.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.article.Services.ArticleService;
import org.example.clients.article.dto.ArticleResponse;
import org.example.clients.article.dto.ArticlesResponse;
import org.example.clients.article.dto.FiltersRequest;
import org.example.clients.article.dto.ViewsResponse;
import org.example.clients.article.entities.Article;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    //todo: add scraping service

    @GetMapping("/testNumber")
    @ResponseStatus(HttpStatus.OK)
    public Integer testNumber(Integer number) {
        return number + 5;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ArticlesResponse getAllArticles() {
        List<Article> articles = new ArrayList<>();
        try {
            articles = articleService.getAllArticles();
            log.info("Fetched all articles from array");
        } catch (Exception e) {
            log.error("Error while getting articles", e);
        }
        return new ArticlesResponse(articles);
    }

    @GetMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse getArticleByUuid(@PathVariable String uuid) {
        Article article = null;
        try {
            article = articleService.getArticleByUuid(uuid);
            log.info("Fetched article by UUID {}", article);
        } catch (Exception e) {
            log.error("Error occurred while getting article", e);
        }
        // Use the builder to construct the ArticleResponse object
        return ArticleResponse.builder()
                .uuid(article.getUuid())
                .url(article.getUrl())
                .title(article.getTitle())
                .content(article.getContent())
                .time(article.getTime())
                .image(article.getImage())
                .source(article.getSource())
                .category(article.getCategory())
                .views(article.getViews())
                .build();
    }

    @PutMapping("/{uuid}/views")
    @ResponseStatus(HttpStatus.OK)
    public ViewsResponse updateArticleViewCount(@PathVariable String uuid) {
        ViewsResponse viewsResponse = new ViewsResponse();
        try {
            viewsResponse = articleService.updateArticleViewCount(uuid);
            log.info("Updated views count for article {}", uuid);
        } catch (Exception e) {
            log.error("Error occurred while updating views count", e);
        }
        return viewsResponse;
    }

    @PostMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public ArticlesResponse getArticlesByFilters(@RequestBody FiltersRequest filterRequest) {
        List<Article> articles = new ArrayList<>();
        try {
            articles = articleService.getArticlesByFilters(filterRequest.categories());
            log.info("Fetched filtered articles");
        } catch (Exception e) {
            log.error("Error occurred while getting articles", e);
        }
        return new ArticlesResponse(articles);
    }
}
