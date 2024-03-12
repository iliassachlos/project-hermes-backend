package org.example.article.dto;

import org.example.article.Entities.Article;

import java.util.List;

public record ArticlesResponse(List<Article> articles) {
}
