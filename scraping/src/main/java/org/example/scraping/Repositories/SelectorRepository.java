package org.example.scraping.Repositories;

import org.example.scraping.Entities.Selector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectorRepository extends MongoRepository<Selector, String> {
}
