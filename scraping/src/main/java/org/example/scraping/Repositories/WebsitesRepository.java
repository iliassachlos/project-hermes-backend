package org.example.scraping.Repositories;

import org.example.scraping.Entities.Website;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebsitesRepository extends MongoRepository<Website, String> {
}
