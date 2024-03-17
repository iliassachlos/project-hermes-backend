package org.example.scraping.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.example.scraping.Entities.Article;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    Article findByUrl(String url);

    List<Article> findByTimeBefore(String time);

    void deleteByTimeBefore(String time);

}
