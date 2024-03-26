package org.example.clients.article.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleResponse {
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
