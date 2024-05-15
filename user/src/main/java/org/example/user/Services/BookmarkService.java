package org.example.user.Services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.ArticleClient;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class BookmarkService {

    private final UserRepository userRepository;
    private final ArticleClient articleClient;

    public ResponseEntity<List<Article>> getAllBookmarkedArticlesById(String id) {
        try {
            User user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                List<Article> bookmarkedArticles = user.getBookmarkedArticles();
                log.info("All bookmarked articles fetched successfully");
                return ResponseEntity.status(HttpStatus.OK).body(bookmarkedArticles);
            }
        } catch (Exception e) {
            log.error("An error occured while fetching bookmarked articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> addBookmarkArticle(String userId, String articleId) {
        try {
            User user = userRepository.findUserById(userId);
            Article article = articleClient.getArticleById(articleId);

            if (user == null) {
                log.error("User with ID {} was not found", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " was not found");
            }

            if (article == null) {
                log.error("Article with ID {} was not found", articleId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Article with ID " + articleId + " was not found");
            }

            Set<Article> bookmarkedArticles = new HashSet<>(user.getBookmarkedArticles());

            // Add the article to the set; duplicates will not be added
            boolean isAdded = bookmarkedArticles.add(article);
            if (!isAdded) {
                log.info("Article with ID {} is already bookmarked for user {}", articleId, userId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Article with ID " + articleId + " is already bookmarked for user " + userId);
            }

            // Save the updated set back to the user's bookmarked articles
            user.setBookmarkedArticles(new ArrayList<>(bookmarkedArticles));
            userRepository.save(user);

            log.info("Bookmark for user {} added successfully", userId);
            return ResponseEntity.status(HttpStatus.OK).body("Bookmark for user " + userId + " added successfully");
        } catch (Exception e) {
            log.error("Error adding bookmark for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding bookmark for user: " + userId);
        }
    }

    public ResponseEntity<String> deleteBookmarkArticleById(String userId, String articleId) {
        try {
            User user = userRepository.findUserById(userId);

            if (user == null) {
                log.error("User with ID {} was not found", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " was not found");
            }

            List<Article> bookmarkedArticles = user.getBookmarkedArticles();
            if (bookmarkedArticles == null) {
                log.error("No bookmarked articles found for user {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bookmarked articles found for user " + userId);
            }

            // Iterate over the bookmarked articles to find the one with the specified ID
            boolean removed = bookmarkedArticles.removeIf(article -> article.getUuid().equals(articleId));
            if (!removed) {
                log.error("Article with ID {} not found in bookmarked articles for user {}", articleId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Article with ID " + articleId + " not found in bookmarked articles for user " + userId);
            }

            // Save the updated user without the deleted bookmarked article
            userRepository.save(user);

            log.info("Bookmark article with ID {} deleted successfully for user {}", articleId, userId);
            return ResponseEntity.status(HttpStatus.OK).body("Bookmark article with ID " + articleId + " deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting bookmark article with ID {} for user: {}", articleId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bookmark for user: " + userId);
        }
    }
}
