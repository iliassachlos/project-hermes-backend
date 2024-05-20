package org.example.article.Services;

import org.example.article.Repositories.ArticleRepository;
import org.example.clients.Entities.Article;
import org.example.clients.dto.article.ViewsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

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
                .sentiment(0.327)
                .build();
    }

    @Test
    public void testGetAllArticles_Success() {
        //Arrange
        List<Article> articles = new ArrayList<>();
        articles.add(article);

        Mockito.when(articleRepository.findAll()).thenReturn(articles);

        //Act
        ResponseEntity<List<Article>> response = articleService.getAllArticles();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(articles, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(articleRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllArticles_NoArticlesFound() {
        // Arrange
        List<Article> articles = Collections.emptyList();
        Mockito.when(articleRepository.findAll()).thenReturn(articles);

        // Act
        ResponseEntity<List<Article>> response = articleService.getAllArticles();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(articleRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllArticles_InternalServerError() {
        //Arrange
        Mockito.when(articleRepository.findAll()).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<List<Article>> response = articleService.getAllArticles();

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(articleRepository, times(1)).findAll();
    }

    @Test
    public void testGetArticleByUuid_Success() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(article);

        //Act
        ResponseEntity<Article> response = articleService.getArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(article, response.getBody());
        assertEquals(uuid, response.getBody().getUuid());

        verify(articleRepository, times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetArticleByUuid_NotFound() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<Article> response = articleService.getArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(articleRepository, times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetArticleByUuid_InternalServerError() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Article> response = articleService.getArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(articleRepository, times(1)).findByUuid(uuid);
    }

    @Test
    public void testUpdateArticleViewCount_Success() {
        //Assert
        String uuid = "uuid-1";
        Integer currentViews = article.getViews();

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(article);
        Mockito.when(articleRepository.save(article));

        //Act
        ResponseEntity<ViewsResponse> response = articleService.updateArticleViewCount(uuid);

        //Arrange
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Article view count updated successfully", response.getBody().getMessage());
        assertEquals(currentViews + 1, response.getBody().getArticleViews());

        verify(articleRepository, times(1)).findByUuid(uuid);
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    public void testUpdateArticleViewCount_ArticleNotFound() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<ViewsResponse> response = articleService.updateArticleViewCount(uuid);

        //Arrange
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Articles with UUID: " + uuid + " not found", response.getBody().getMessage());

        verify(articleRepository, times(1)).findByUuid(uuid);
        verify(articleRepository, times(0)).save(article);
    }

    @Test
    public void testUpdateArticleViewCount_InternalServerError() {
        //Assert
        Mockito.when(articleRepository.findByUuid("uuid-1")).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<ViewsResponse> response = articleService.updateArticleViewCount("uuid-1");

        //Arrange
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to update views count", response.getBody().getMessage());

        verify(articleRepository, times(1)).findByUuid("uuid-1");
    }

    @Test
    public void testDeleteArticleByUuid_Success() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(article);

        //Act
        ResponseEntity<String> response = articleService.deleteArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Article with UUID: " + uuid + " deleted successfully", response.getBody());

        verify(articleRepository, times(1)).findByUuid(uuid);
        verify(articleRepository, times(1)).delete(article);
    }

    @Test
    public void testDeleteArticleByUuid_ArticleNotFound() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<String> response = articleService.deleteArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Article with " + uuid + " not found", response.getBody());

        verify(articleRepository, times(1)).findByUuid(uuid);
        verify(articleRepository, times(0)).delete(article);
    }

    @Test
    public void testDeleteArticleByUuid_InternalServerError() {
        //Assert
        String uuid = "uuid-1";

        Mockito.when(articleRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<String> response = articleService.deleteArticleByUuid(uuid);

        //Arrange
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(articleRepository, times(1)).findByUuid(uuid);
        verify(articleRepository, times(0)).delete(article);
    }
}
