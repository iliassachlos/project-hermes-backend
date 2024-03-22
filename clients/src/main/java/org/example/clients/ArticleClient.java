package org.example.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient("article")
public interface ArticleClient {

    @GetMapping("api/articles/all")
    @ResponseStatus(HttpStatus.OK)
    ArticlesResponse getAllArticles();

    @GetMapping("api/articles/testNumber")
    @ResponseStatus(HttpStatus.OK)
    Integer testNumber(Integer number);
}
