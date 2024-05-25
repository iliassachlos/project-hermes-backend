package org.example.scraping.Service;

import org.example.amqp.RabbitMQMessageProducer;
import org.example.clients.Entities.Article;
import org.example.scraping.Repositories.ArticleRepository;
import org.example.scraping.Repositories.WebsitesRepository;
import org.example.scraping.utils.Scraper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class ScrapingServiceTest {

    @InjectMocks
    private ScrapingService scrapingService;

    @Mock
    private Scraper scraper;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private WebsitesRepository websitesRepository;

    @Mock
    private RabbitMQMessageProducer rabbitMQMessageProducer;

    private Article article;

    @BeforeEach
    void setUp() {
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
    void testSaveArticles_Success() {
        // Arrange
        Article article = new Article();
        List<Article> articles = new ArrayList<>();
        articles.add(article);

        Mockito.when(articleRepository.findByUrl(Mockito.anyString())).thenReturn(null);
        Mockito.when(articleRepository.save(Mockito.any())).thenReturn(article);

        // Act
        scrapingService.saveArticles(articles);

        // Assert
        Mockito.verify(articleRepository, Mockito.times(1)).save(article);
    }

    @Test
    void testDeleteOldArticles_Success() {
        // Arrange
        List<Article> oldArticles = new ArrayList<>();
        oldArticles.add(article);

        Mockito.when(articleRepository.findByTimeBefore(Mockito.anyString())).thenReturn(oldArticles);

        // Act
        scrapingService.deleteOldArticles();

        // Assert
        Mockito.verify(articleRepository, Mockito.times(1)).deleteByTimeBefore(Mockito.anyString());
        Mockito.verify(articleRepository, Mockito.times(1)).deleteByTimeBefore(Mockito.anyString());
    }

    @Test
    void testSaveToElastic_Success() {
        // Arrange
        List<Article> articles = new ArrayList<>();
        articles.add(article);

        Mockito.when(articleRepository.findAll()).thenReturn(articles);

        // Act
        ResponseEntity<String> response = scrapingService.saveToElastic();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Save completed", response.getBody());

        Mockito.verify(rabbitMQMessageProducer, Mockito.times(1)).publish(articles, "internal.exchange", "internal.elastic-saver.routing-key");
    }

    @Test
    void testSaveToElastic_ArticlesNotFound() {
        // Arrange
        List<Article> articles = new ArrayList<>();

        Mockito.when(articleRepository.findAll()).thenReturn(articles);

        // Act
        ResponseEntity<String> response = scrapingService.saveToElastic();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Mockito.verify(articleRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testSaveToElastic_InternalServerError() {
        // Arrange
        List<Article> articles = new ArrayList<>();
        articles.add(article);

        Mockito.when(articleRepository.findAll()).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<String> response = scrapingService.saveToElastic();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
