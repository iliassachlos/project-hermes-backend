package org.example.article.Repositories;

import org.example.clients.article.Entities.Article;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {


    Article findArticleById(String id);

    List<Article> findArticlesByCategoryIn(List<String> categories);
}
