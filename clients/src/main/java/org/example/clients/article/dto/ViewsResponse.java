package org.example.clients.article.dto;

import lombok.Data;

@Data
public class ViewsResponse {
    private String message;
    private Integer articleViews;
}
