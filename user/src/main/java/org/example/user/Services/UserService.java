package org.example.user.Services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.ArticleClient;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.example.clients.dto.user.LoginUserResponse;
import org.example.clients.dto.user.RegisterUserResponse;
import org.example.user.utilities.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ArticleClient articleClient;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<RegisterUserResponse> registerUser(String username, String email, String password) {
        //Check if user credentials are not empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterUserResponse.builder()
                            .message("Please provide all the required fields")
                            .build()
            );
        }
        try {
            //Check if user already exists in database
            User existingUser = userRepository.findUserByEmail(email);
            if (existingUser != null) {
                log.error("User already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        RegisterUserResponse.builder()
                                .message("User already exists")
                                .build()
                );
            }

            //Encrypt password
            String encryptedPassword = passwordEncoder.encode(password);

            //Build new user
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(encryptedPassword)
                    .isAdmin(false)
                    .bookmarkedArticles(new ArrayList<>())
                    .savedQueries(new ArrayList<>())
                    .build();

            //Save new user to database
            userRepository.save(newUser);

            // Generate JWT token
            String token = JwtTokenUtil.generateToken(newUser.getId());
            log.info("User created successfully {}", email);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    RegisterUserResponse.builder()
                            .message("User created successfully")
                            .user(newUser)
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error occurred while registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RegisterUserResponse.builder()
                            .message("Error occurred while registering user")
                            .build()
            );
        }
    }

    public ResponseEntity<LoginUserResponse> loginUser(String email, String password) {
        // Check if user credentials are not empty
        if (email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    LoginUserResponse.builder()
                            .message("Please provide all the required credentials")
                            .build()
            );
        }
        try {
            // Check if user exists in the database
            User foundUser = userRepository.findUserByEmail(email);
            if (foundUser == null) {
                log.error("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        LoginUserResponse.builder()
                                .message("User was not found. Please check your credentials or register")
                                .build()
                );
            }

            // Check found user's encrypted password with the provided password
            String encryptedPassword = foundUser.getPassword();
            if (passwordEncoder.matches(password, encryptedPassword)) {
                // Passwords match, login successful
                String token = JwtTokenUtil.generateToken(foundUser.getId());

                log.info("Login successful");
                return ResponseEntity.status(HttpStatus.OK).body(
                        LoginUserResponse.builder()
                                .message("Login successful")
                                .user(foundUser)
                                .token(token)
                                .build()
                );
            } else {
                // Passwords don't match
                log.error("Invalid password. Please try again!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        LoginUserResponse.builder()
                                .message("Invalid password. Please try again!")
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("An error occurred while login user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    LoginUserResponse.builder()
                            .message("An error occurred while login user")
                            .build()
            );
        }
    }

    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            log.info("All users fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            log.error("Error occurred while getting all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<User> getUserById(String id) {
        try {
            //Check if user exists in database
            User user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(user);
            }
        } catch (Exception e) {
            log.error("Error occurred while finding user by id {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Boolean> isUserAdmin(String id) {
        try {
            User user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(user.getIsAdmin());
        } catch (Exception e) {
            log.error("An error occurred while checking if user is admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    public ResponseEntity<List<String>> getAllQueries(String id) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<String> queries = existingUser.getSavedQueries();
            log.info("Fetched all queries for user {}", id);
            return ResponseEntity.status(HttpStatus.OK).body(queries);
        } catch (Exception e) {
            log.error("An error occurred while getting saved queries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> addQuery(String id, String query) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " was not found");
            }
            List<String> existingQueries = existingUser.getSavedQueries();
            existingQueries.add(query);
            existingUser.setSavedQueries(existingQueries);
            userRepository.save(existingUser);
            log.info("Query {} added successfully for user with id {}", query, id);
            return ResponseEntity.status(HttpStatus.OK).body("Query " + query + " added onUser with ID " + id + " added successfully");
        } catch (Exception e) {
            log.error("An error occurred while adding query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteQuery(String id, Integer index) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " was not found");
            }
            List<String> existingQueries = existingUser.getSavedQueries();
            existingQueries.remove(existingQueries.get(index));
            existingUser.setSavedQueries(existingQueries);
            userRepository.save(existingUser);
            log.info("Query {} deleted successfully for user with id {}", index, id);
            return ResponseEntity.status(HttpStatus.OK).body("Query deleted successfully for user with id " + id);
        } catch (Exception e) {
            log.error("An error occurred while deleting query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

