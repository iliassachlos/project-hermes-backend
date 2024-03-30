package org.example.scraping.Repositories;

import org.example.clients.article.Entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    Article findArticleByUrl(String url);

    List<Article> findArticlesByTimeBefore(String time);

    void deleteArticlesByTimeBefore(String time);

}
