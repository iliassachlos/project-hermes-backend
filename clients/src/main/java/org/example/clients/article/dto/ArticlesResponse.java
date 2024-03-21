package org.example.clients.article.dto;

import org.example.clients.article.entities.Article;

import java.util.List;

public record ArticlesResponse(List<Article> articles) {
}
