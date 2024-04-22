package org.example.clients;

import org.example.clients.Entities.PreProcessedArticle;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("elasticsearch")
public interface ElasticsearchClient {

    @PostMapping("api/elastic/save")
    void saveArticles(@RequestBody List<PreProcessedArticle> preProcessedArticles);
}
