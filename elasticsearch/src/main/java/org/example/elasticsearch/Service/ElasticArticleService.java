package org.example.elasticsearch.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.Article;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Repository.ElasticArticleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ElasticArticleService {
    private final ElasticArticleRepository articleRepository;

    private ElasticArticle transformArticleToElasticArticle(Article article) {
        return ElasticArticle.builder()
                .id(article.getId())
                .uuid(article.getUuid())
                .url(article.getUrl())
                .title(article.getTitle())
                .content(article.getContent())
                .image(article.getImage())
                .source(article.getSource())
                .category(article.getCategory())
                .views(article.getViews())
                .build();
    }

    public void saveArticles(List<Article> articles) {
        List<ElasticArticle> elasticArticles = new ArrayList<>();
        try {
            for (Article article : articles) {
                elasticArticles.add(transformArticleToElasticArticle(article));
            }
//            articleRepository.saveAll(elasticArticles);
            for (ElasticArticle elasticArticle : elasticArticles) {
                articleRepository.save(elasticArticle);
                log.info("Saved to ES article: {}", elasticArticle.getTitle());
            }
            log.info("All Articles were saved to Elasticsearch");
        } catch (Exception e) {
            log.error("An error occurred while saving articles to Elasticsearch", e);
        }
    }
}
