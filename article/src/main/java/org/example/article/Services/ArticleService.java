package org.example.article.Services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.amqp.RabbitMQMessageProducer;
import org.example.article.Repositories.ArticleRepository;
import org.example.clients.ElasticsearchClient;
import org.example.clients.Entities.Article;
import org.example.clients.UserClient;
import org.example.clients.dto.article.ViewsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;

    private final ElasticsearchClient elasticsearchClient;
    private final UserClient userClient;

    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            //Get all articles from MongoDB
            List<Article> articles = articleRepository.findAll();
            if (articles.isEmpty()) {
                log.error("No articles found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                Collections.reverse(articles);
                log.info("Fetched all articles");
                return ResponseEntity.status(HttpStatus.OK).body(articles);
            }
        } catch (Exception e) {
            log.error("Error occurred while getting articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Article> getArticleByUuid(String uuid) {
        try {
            Article article = articleRepository.findByUuid(uuid);
            log.info("Fetched article by uuid {} ", uuid);
            return ResponseEntity.status(HttpStatus.OK).body(article);
        } catch (Exception e) {
            log.error("Error occurred while getting article", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    public ResponseEntity<ViewsResponse> updateArticleViewCount(String uuid) {

        try {
            // Retrieve the article by UUID
            Article article = articleRepository.findByUuid(uuid);
            if (article == null) {
                // If article not found, set appropriate message in response
                log.error("Articles with {} not found", uuid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ViewsResponse.builder()
                                .message("Articles with UUID: " + uuid + " not found")
                                .build()
                );
            } else {
                // Increment the view count
                int updatedViews = article.getViews() + 1;
                article.setViews(updatedViews);
                articleRepository.save(article);

                // Prepare the response
                log.info("Article view count updated successfully");
                return ResponseEntity.status(HttpStatus.OK).body(
                        ViewsResponse.builder()
                                .message("Article view count updated successfully")
                                .articleViews(updatedViews)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Error occurred while updating views count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ViewsResponse.builder()
                            .message("Failed to update views count")
                            .build()
            );
        }
    }
}
