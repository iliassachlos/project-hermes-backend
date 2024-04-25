package org.example.scraping.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "websites")
public class Website {
    @Id
    private ObjectId id;

    private String uuid = UUID.randomUUID().toString();

    private String title;

    private String icon;

    private String value;

    private Map<String, String> categories;
}