package org.example.elasticsearch.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@AllArgsConstructor
@Document(indexName = "articles")
public class ElasticArticle {
    @Id
    private String id;

    private String uuid;

    private String url;

    private String title;

    private String content;

    private String time;

    private String image;

    private String source;

    private String category;

    private Integer views;
}
