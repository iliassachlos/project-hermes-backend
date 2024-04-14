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

    public RegisterUserResponse registerUser(String username, String email, String password) {
        User newUser = new User();
        String token = null;

        //Check if user credentials are not empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return RegisterUserResponse.builder()
                    .message("Please provide all the required fields")
                    .build();
        }
        try {
            //Check if user already exists in database
            User existingUser = userRepository.findUserByEmail(email);
            if (existingUser != null) {
                log.error("User already exists");
                return RegisterUserResponse.builder()
                        .message("User already exists")
                        .build();
            }

            //Encrypt password
            String encryptedPassword = passwordEncoder.encode(password);

            //Build new user
            newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(encryptedPassword)
                    .isAdmin(false)
                    .bookmarkedArticles(new ArrayList<>())
                    .build();

            //Save new user to database
            userRepository.save(newUser);

            // Generate JWT token
            token = JwtTokenUtil.generateToken(newUser.getId());
        } catch (Exception e) {
            log.error("Error occurred while registering user", e);
        }

        log.info("User created successfully {}", email);
        return RegisterUserResponse.builder()
                .message("User created successfully")
                .user(newUser)
                .token(token)
                .build();
    }

    public LoginUserResponse loginUser(String email, String password) {
        User foundUser = new User();
        String token = null;

        // Check if user credentials are not empty
        if (email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return LoginUserResponse.builder()
                    .message("Please provide all the required fields")
                    .build();
        }
        try {
            // Check if user exists in the database
            foundUser = userRepository.findUserByEmail(email);
            if (foundUser == null) {
                log.error("User not found for email: {}", email);
                return LoginUserResponse.builder()
                        .message("User not found. Please check your credentials or register")
                        .build();
            }

            // Check found user's encrypted password with the provided password
            String encryptedPassword = foundUser.getPassword();
            if (passwordEncoder.matches(password, encryptedPassword)) {
                // Passwords match, login successful
                token = JwtTokenUtil.generateToken(foundUser.getId());
            } else {
                // Passwords don't match
                log.error("Invalid pasword. Please try again!");
                return LoginUserResponse.builder()
                        .message("Invalid password. Please try again!")
                        .build();
            }
        } catch (Exception e) {
            log.error("An error occurred while login user", e);
        }

        log.info("Login successful");
        return LoginUserResponse.builder()
                .message("Login successful")
                .user(foundUser)
                .token(token)
                .build();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            users = userRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while getting all users", e);
        }

        log.info("All users fetched successfully");
        return users;
    }

    public User getUserById(String id) {
        User user = new User();
        try {
            //Check if user exists in database
            user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while finding user by id {}", id);
        }
        return user;
    }

    public List<Article> getAllBookmarkedArticlesById(String userId) {
        List<Article> bookmarkedArticles = new ArrayList<>();
        try {
            User user = userRepository.findUserById(userId);
            if (user == null) {
                log.error("User was not found");
                return null;
            }
            bookmarkedArticles = user.getBookmarkedArticles();
        } catch (Exception e) {
            log.error("An error occured while fetching bookmarked articles", e);
        }
        return bookmarkedArticles;
    }

    public String addBookmarkArticle(String userId, String articleId) {
        try {
            User user = userRepository.findUserById(userId);
            Article article = articleClient.getArticleById(articleId);

            if (user == null) {
                log.error("User with ID {} was not found", userId);
                return "User with ID " + userId + " not found";
            }

            if (article == null) {
                log.error("Article with ID {} was not found", articleId);
                return "Article with ID " + articleId + " not found";
            }

            Set<Article> bookmarkedArticles = new HashSet<>(user.getBookmarkedArticles());

            // Add the article to the set; duplicates will not be added
            boolean isAdded = bookmarkedArticles.add(article);
            if (!isAdded) {
                log.info("Article with ID {} is already bookmarked for user {}", articleId, userId);
                return "Article with ID " + articleId + " is already bookmarked for user " + userId;
            }

            // Save the updated set back to the user's bookmarked articles
            user.setBookmarkedArticles(new ArrayList<>(bookmarkedArticles));
            userRepository.save(user);

            log.info("Bookmark for user {} added successfully", userId);
            return "Bookmark for user " + userId + " added successfully";
        } catch (Exception e) {
            log.error("Error adding bookmark for user: {}", userId, e);
            return "Error adding bookmark for user: " + userId;
        }
    }

    public String deleteBookmarkArticleById(String userId, String articleId) {
        try {
            User user = userRepository.findUserById(userId);
            if (user == null) {
                log.error("User with ID {} was not found", userId);
                return "User with ID " + userId + " not found";
            }

            List<Article> bookmarkedArticles = user.getBookmarkedArticles();
            if (bookmarkedArticles == null) {
                log.error("No bookmarked articles found for user {}", userId);
                return "No bookmarked articles found for user " + userId;
            }

            // Iterate over the bookmarked articles to find the one with the specified ID
            boolean removed = bookmarkedArticles.removeIf(article -> article.getId().equals(articleId));
            if (!removed) {
                log.error("Article with ID {} not found in bookmarked articles for user {}", articleId, userId);
                return "Article with ID " + articleId + " not found in bookmarked articles for user " + userId;
            }

            // Save the updated user without the deleted bookmarked article
            userRepository.save(user);

            log.info("Bookmark article with ID {} deleted successfully for user {}", articleId, userId);
            return "Bookmark article with ID " + articleId + " deleted successfully for user " + userId;
        } catch (Exception e) {
            log.error("Error deleting bookmark article with ID {} for user: {}", articleId, userId, e);
            return "Error deleting bookmark article for user " + userId;
        }
    }


}

