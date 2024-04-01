package org.example.scraping.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "selector")
public class Selector {
    @Id
    @Column(name = "id", updatable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "name",nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "selector")
    private Set<SelectorSelectors> selectors;
}
