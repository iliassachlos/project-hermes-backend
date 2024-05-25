package org.example.elasticsearch.Service;

import org.example.clients.Entities.Article;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Repository.ElasticArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class ElasticArticleServiceTest {

    @InjectMocks
    private ElasticArticleService elasticArticleService;

    @Mock
    private ElasticArticleRepository elasticArticleRepository;

    private Article article;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        article = Article.builder()
                .id("id-1")
                .uuid("uuid-1")
                .url("https://example.com")
                .title("title-1")
                .content("content-1")
                .time("2024-08-12")
                .image("https://example.com/images/1")
                .source("source-1")
                .category("category-1")
                .views(8257)
                .sentimentScore(2)
                .build();
    }

    @Test
    public void testSaveArticles_Success() {
        // Arrange
        List<Article> articles = new ArrayList<>();
        articles.add(article);

        List<ElasticArticle> elasticArticles = new ArrayList<>();
        elasticArticles.add(elasticArticleService.transformArticleToElasticArticle(article));

        Mockito.when(elasticArticleRepository.saveAll(elasticArticles)).thenReturn(elasticArticles);

        // Act
        elasticArticleService.saveArticles(articles);

        // Assert
        assertEquals(elasticArticles.get(0).getId(),articles.get(0).getId());
        assertEquals(articles.size(), elasticArticles.size());

        Mockito.verify(elasticArticleRepository, Mockito.times(1)).saveAll(elasticArticles);
    }
}
