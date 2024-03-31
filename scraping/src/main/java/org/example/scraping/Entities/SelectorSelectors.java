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
@Table(name = "selector_selectors")
public class SelectorSelectors {
    @Id
    @Column(name = "id",updatable = false,nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "selector",nullable = false)
    private String selector;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "selector_id",referencedColumnName = "id",nullable = false)
    private Selector selectors;
}