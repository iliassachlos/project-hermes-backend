package org.example.scraping.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "websites")
public class Website {
    @Id
    private String id;
    private String title;
    private String icon;
    private String value;
    private Map<String, String> categories;
}
