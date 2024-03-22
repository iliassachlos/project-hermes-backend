package org.example.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@FeignClient("article")
public interface ArticleClient {

    @GetMapping("api/articles/all")
    @ResponseStatus(HttpStatus.OK)
    ArticlesResponse getAllArticles();

    @PostMapping("api/articles/save")
    @ResponseStatus(HttpStatus.OK)
    List<Article> saveArticles(@RequestBody List<Article> articles);
}
