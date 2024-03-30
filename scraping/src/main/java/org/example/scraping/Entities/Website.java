package org.example.scraping.Entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "websites")
public class Website {
    @Id
    private String id = UUID.randomUUID().toString();

    private String title;

    private String icon;

    private String value;

    @ElementCollection
    private Map<String, String> categories;
}
