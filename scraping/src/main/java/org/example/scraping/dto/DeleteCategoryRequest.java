package org.example.scraping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCategoryRequest {
    private String id;
    private HashMap<String,String> categories;
}
