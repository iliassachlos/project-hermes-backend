package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.clients.dto.user.*;
import org.example.user.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkUserServiceStatus() {
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

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

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/admin-status")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable String id) {
        return userService.isUserAdmin(id);
    }

    @GetMapping("/bookmarks/{id}")
    public ResponseEntity<List<Article>> getAllBookmarkedArticlesById(@PathVariable String id) {
        return userService.getAllBookmarkedArticlesById(id);
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

    @GetMapping("/queries/{id}/all")
    public ResponseEntity<List<String>> getAllQueries(@PathVariable String id) {
        return userService.getAllQueries(id);
    }

    @PostMapping("/queries/add")
    public ResponseEntity<String> addQuery(@RequestBody AddQueryRequest addQueryRequest) {
        String userId = addQueryRequest.getId();
        String query = addQueryRequest.getQuery();
        return userService.addQuery(userId, query);
    }

    @DeleteMapping("/queries/{id}/{index}/delete")
    public ResponseEntity<String> deleteQuery(@PathVariable String id, @PathVariable Integer index) {
        return userService.deleteQuery(id, index);
    }
}
