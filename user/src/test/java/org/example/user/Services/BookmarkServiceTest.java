package org.example.user.Services;

import org.example.clients.ArticleClient;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleClient articleClient;

    @InjectMocks
    private BookmarkService bookmarkService;

    private User user;
    private Article article;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        article = new Article(
                "id-1",
                "uuid-1",
                "https://example.com/examples/1",
                "title 1",
                "content 1",
                "2024-05-14",
                "http://example.com/image1",
                "source1",
                "category1",
                72,
                3
        );
        List<Article> bookmarkedArticles = new ArrayList<>();
        bookmarkedArticles.add(article);

        user = new User(
                "user123",
                "user123@test.com",
                "user123",
                "12345",
                false,
                bookmarkedArticles,
                new ArrayList<>()
        );
    }

    @Test
    public void testGetAllBookmarkedArticlesById_Success() {
        // Arrange
        String userId = "user123";
        List<Article> expectedArticles = user.getBookmarkedArticles();
        when(userRepository.findUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<List<Article>> responseEntity = bookmarkService.getAllBookmarkedArticlesById(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedArticles, responseEntity.getBody());
    }

    @Test
    public void testGetAllBookmarkedArticlesById_UserNotFound() {
        // Arrange
        String userId = "user-200";
        when(userRepository.findUserById(userId)).thenReturn(null);

        // Act
        ResponseEntity<List<Article>> responseEntity = bookmarkService.getAllBookmarkedArticlesById(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testAddBookmarkArticle_Success() {
        // Arrange
        User userWithoutBookmarks = new User(
                "user123",
                "user123@test.com",
                "user123",
                "12345",
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        when(userRepository.findUserById(userWithoutBookmarks.getId())).thenReturn(userWithoutBookmarks);
        when(articleClient.getArticleById(article.getId())).thenReturn(article);

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.addBookmarkArticle(userWithoutBookmarks.getId(), article.getId());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Bookmark for user " + userWithoutBookmarks.getId() + " added successfully", responseEntity.getBody());
    }

    @Test
    public void testAddBookmarkArticle_UserNotFound() {
        // Arrange
        when(userRepository.findUserById(user.getId())).thenReturn(null);

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.addBookmarkArticle(user.getId(), article.getId());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + user.getId() + " was not found", responseEntity.getBody());
    }

    @Test
    public void testDeleteBookmarkArticleById_UserNotFound() {
        // Arrange
        String userId = "nonExistingUser";
        String articleId = article.getId();
        when(userRepository.findUserById(userId)).thenReturn(null);

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.deleteBookmarkArticleById(userId, articleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + userId + " was not found", responseEntity.getBody());
    }

    @Test
    public void testDeleteBookmarkArticleById_ArticleNotFound() {
        // Arrange
        String userId = user.getId();
        String nonExistingArticleId = "nonExistingArticle123";
        when(userRepository.findUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.deleteBookmarkArticleById(userId, nonExistingArticleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Article with ID " + nonExistingArticleId + " not found in bookmarked articles for user " + userId, responseEntity.getBody());
    }

    @Test
    public void testDeleteBookmarkArticleById_InternalServerError() {
        // Arrange
        String userId = user.getId();
        String articleId = article.getId();
        when(userRepository.findUserById(userId)).thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.deleteBookmarkArticleById(userId, articleId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error deleting bookmark for user: " + userId, responseEntity.getBody());
    }
}
