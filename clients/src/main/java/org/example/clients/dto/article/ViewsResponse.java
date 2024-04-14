package org.example.clients.dto.article;

import lombok.Data;

@Data
public class ViewsResponse {
    private String message;
    private Integer articleViews;
}
