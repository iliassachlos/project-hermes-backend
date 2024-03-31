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
@Table(name = "article")
public class Article {
    @Id
    @Column(name = "id", updatable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "time")
    private String time;

    @Column(name = "image")
    private String image;

    @Column(name = "source")
    private String source;

    @Column(name = "category")
    private String category;

    @Column(name = "views")
    private Integer views;
}
