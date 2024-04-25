package org.example.scraping.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "selectors")
public class Selector {
    @Id
    private String id;

    private String uuid = UUID.randomUUID().toString();

    private String name;

    private List<String> selectors;
}
