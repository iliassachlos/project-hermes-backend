package org.example.scraping.Repositories;

import org.example.clients.Entities.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    Article findByUrl(String url);

    List<Article> findByTimeBefore(String time);

    void deleteByTimeBefore(String time);

}
