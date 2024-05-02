package org.example.clients.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pre-processed-articles")
public class PreProcessedArticle {
    @Id
    private String id;

    private String uuid = UUID.randomUUID().toString();

    private String url;

    private String title;

    private String content;

    private String time;

    private String image;

    private String source;

    private String category;

    private Integer views;
}
