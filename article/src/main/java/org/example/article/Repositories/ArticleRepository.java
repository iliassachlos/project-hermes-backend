package org.example.article.Repositories;

import org.example.article.Entities.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    Article findByUrl(String url);

    Article findByUuid(String uuid);

    List<Article> findByTimeBefore(LocalDate time);

    void deleteByTimeBefore(LocalDate time);

    List<Article> findByCategoryIn(List<String> categories);
}
