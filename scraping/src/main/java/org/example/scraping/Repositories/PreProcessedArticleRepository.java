package org.example.scraping.Repositories;

import org.example.clients.Entities.PreProcessedArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PreProcessedArticleRepository extends MongoRepository<PreProcessedArticle, String> {
    PreProcessedArticle findByUrl(String url);

    List<PreProcessedArticle> findByTimeBefore(String time);

    void deleteByTimeBefore(String time);
}
