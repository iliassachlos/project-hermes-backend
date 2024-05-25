package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.Article;
import org.example.clients.dto.user.BookmarkRequest;
import org.example.user.Service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/users/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/{id}")
    public ResponseEntity<List<Article>> getAllBookmarkedArticlesById(@PathVariable String id) {
        return bookmarkService.getAllBookmarkedArticlesById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBookmarkArticle(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return bookmarkService.addBookmarkArticle(userId, articleId);
    }

    @PutMapping("/delete")
    public ResponseEntity<String> deleteBookmarkArticleById(@RequestBody BookmarkRequest bookmarkRequest) {
        String userId = bookmarkRequest.getUserId();
        String articleId = bookmarkRequest.getArticleId();
        return bookmarkService.deleteBookmarkArticleById(userId, articleId);
    }
}
