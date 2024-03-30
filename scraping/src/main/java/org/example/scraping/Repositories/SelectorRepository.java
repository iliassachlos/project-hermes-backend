package org.example.scraping.Repositories;

import org.example.scraping.Entities.Selector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectorRepository extends JpaRepository<Selector, String> {
}
