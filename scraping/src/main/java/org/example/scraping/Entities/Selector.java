package org.example.scraping.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "selectors")
public class Selector {
    @Id
    private String id = UUID.randomUUID().toString();

    private String name;

    @ElementCollection
    private List<String> selectors;
}
