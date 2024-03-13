package org.example.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {
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
