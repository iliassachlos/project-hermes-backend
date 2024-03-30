package org.example.clients.article.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {
    @Id
    private String id = UUID.randomUUID().toString();

    private String url;

    private String title;

    private String content;

    private String time;

    private String image;

    private String source;

    private String category;

    private Integer views;
}
