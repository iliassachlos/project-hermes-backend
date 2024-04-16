package org.example.scraping.Repositories;

import org.example.scraping.Entities.Website;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsitesRepository extends MongoRepository<Website, String> {
    Website findByTitle(String title);
}
