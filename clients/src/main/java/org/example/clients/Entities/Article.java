package org.example.clients.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "articles")
public class Article {
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

    private Double sentiment;
}
