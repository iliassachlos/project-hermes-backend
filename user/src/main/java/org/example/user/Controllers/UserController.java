package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.clients.dto.user.*;
import org.example.user.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        String username = registerUserRequest.getUsername();
        String email = registerUserRequest.getEmail();
        String password = registerUserRequest.getPassword();
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        return userService.loginUser(email, password);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/bookmarks/{userId}")
    public ResponseEntity<List<Article>> getAllBookmarkedArticlesById(@PathVariable String userId) {
        return userService.getAllBookmarkedArticlesById(userId);
    }

    @PostMapping("/bookmarks/add")
    public ResponseEntity<String> addBookmarkArticle(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return userService.addBookmarkArticle(userId, articleId);
    }

    @PutMapping("/bookmarks/delete")
    public ResponseEntity<String> deleteBookmarkArticleById(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return userService.deleteBookmarkArticleById(userId, articleId);
    }
}
