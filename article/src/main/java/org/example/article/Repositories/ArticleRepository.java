package org.example.article.Repositories;

import org.example.clients.article.Entities.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    Article findByUuid(String uuid);

    List<Article> findByCategoryIn(List<String> categories);
}
