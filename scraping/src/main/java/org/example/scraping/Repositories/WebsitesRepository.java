package org.example.scraping.Repositories;

import org.example.scraping.Entities.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebsitesRepository extends JpaRepository<Website, String> {
}
