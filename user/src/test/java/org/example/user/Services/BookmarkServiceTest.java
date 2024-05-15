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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllBookmarkedArticlesById() {
        //Arrange
        String userId = "user123";
        List<Article> expectedArticles = new ArrayList<>();
        List<String> queries = new ArrayList<>();
        expectedArticles.add(new Article(
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
                0.124)
        );
        when(userRepository.findUserById(userId)).thenReturn(new User(
                "user-1",
                "user@test.com",
                "userTest",
                "12345",
                false,
                expectedArticles,
                queries)
        );

        // Invoke service method
        ResponseEntity<List<Article>> responseEntity = bookmarkService.getAllBookmarkedArticlesById(userId);

        // Verify the response
        //Check if status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        //Check if articles equals to expectedArticles
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
        when(userRepository.findUserById("user123")).thenReturn(new User(
                "user123",
                "user123@test.com",
                "user123",
                "12345",
                false,
                new ArrayList<>(),
                new ArrayList<>()));
        when(articleClient.getArticleById("article123")).thenReturn(new Article(
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
                0.124)
        );

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.addBookmarkArticle("user123", "article123");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Bookmark for user user123 added successfully", responseEntity.getBody());
    }

    @Test
    public void testAddBookmarkArticle_UserNotFound() {
        // Arrange
        when(userRepository.findUserById("user123")).thenReturn(null);

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.addBookmarkArticle("user123", "article123");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID user123 was not found", responseEntity.getBody());
    }

    @Test
    public void testDeleteBookmarkArticleById_UserNotFound() {
        // Arrange
        String userId = "nonExistingUser";
        String articleId = "article123";
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
        String userId = "user123";
        String nonExistingArticleId = "nonExistingArticle123";
        List<Article> bookmarkedArticles = new ArrayList<>();
        bookmarkedArticles.add(new Article(
                "article123",
                "uuid-1",
                "https://example.com/examples/1",
                "title 1",
                "content 1",
                "2024-05-14",
                "http://example.com/image1",
                "source1",
                "category1",
                72,
                0.124)
        );
        User user = new User(
                userId,
                "user123@test.com",
                "user123",
                "12345",
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
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
        String userId = "user123";
        String articleId = "article123";
        when(userRepository.findUserById(userId)).thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<String> responseEntity = bookmarkService.deleteBookmarkArticleById(userId, articleId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error deleting bookmark for user: " + userId, responseEntity.getBody());
    }

}
