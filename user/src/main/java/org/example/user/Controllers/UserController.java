package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.User;
import org.example.clients.dto.user.*;
import org.example.user.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        String username = registerUserRequest.getUsername();
        String email = registerUserRequest.getEmail();
        String password = registerUserRequest.getPassword();
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.FOUND)
    public LoginUserResponse loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        return userService.loginUser(email, password);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/bookmarks/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> getAllBookmarkedArticlesById(@PathVariable String userId) {
        return userService.getAllBookmarkedArticlesById(userId);
    }

    @PostMapping("/bookmarks/add")
    @ResponseStatus(HttpStatus.OK)
    public String addBookmarkArticle(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return userService.addBookmarkArticle(userId, articleId);
    }

    @DeleteMapping("/bookmarks/delete")
    @ResponseStatus(HttpStatus.OK)
    public String deleteBookmarkArticleById(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return userService.deleteBookmarkArticleById(userId, articleId);
    }
}
