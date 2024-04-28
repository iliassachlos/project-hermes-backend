package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class WebsiteService {
    private final WebsitesRepository websitesRepository;

    public ResponseEntity<List<Website>> getAllWebsites() {
        try {
            List<Website> websites = websitesRepository.findAll();
            if (websites.isEmpty()) {
                log.error("No websites found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            log.info("Fetched {} websites", websites.size());
            return ResponseEntity.status(HttpStatus.OK).body(websites);
        } catch (Exception e) {
            log.error("An error occurred while getting websites", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> getWebsiteByUUID(String uuid) {
        try {
            Website website = websitesRepository.findByUuid(uuid);
            if (website == null) {
                log.error("Website with id {} not found", uuid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            log.info("Fetched website with id: {}", website);
            return ResponseEntity.status(HttpStatus.OK).body(website);
        } catch (Exception e) {
            log.error("An error occurred while getting website by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Website> editWebsite(Website editedWebsite) {
        try {
            String websiteId = editedWebsite.getId().toString();
            Website existingWebsite = websitesRepository.findById(websiteId).orElse(null);
            if (existingWebsite == null) {
                log.error("Website with id {} not found", websiteId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Website.builder()
                    .title(editedWebsite.getTitle())
                    .icon(editedWebsite.getIcon())
                    .value(editedWebsite.getValue())
                    .categories(editedWebsite.getCategories())
                    .build();
            Website savedWebsite = websitesRepository.save(existingWebsite);
            log.info("Saved website with id: {}", savedWebsite);
            return ResponseEntity.status(HttpStatus.OK).body(savedWebsite);
        } catch (Exception e) {
            log.error("An error occurred while editing website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> saveWebsite(Website website) {
        try {
            Website savedWebsite = websitesRepository.save(website);
            log.info("Created a new website with title = {}", website.getTitle());
            return ResponseEntity.status(HttpStatus.OK).body(savedWebsite);
        } catch (Exception e) {
            log.error("Error occurred while saving website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> saveWebsiteCategory(String id, String category, String url) {
        try {
            Website existingWebsite = websitesRepository.findByUuid(id);
            if (existingWebsite == null) {
                log.error("Website with id {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            existingWebsite.getCategories().put(category, url);
            websitesRepository.save(existingWebsite);
            log.info("Saved website with id: {}", existingWebsite);
            return ResponseEntity.status(HttpStatus.OK).body(existingWebsite);
        } catch (Exception e) {
            log.error("An error occurred while saving website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> deleteWebsiteCategory(String id, String categoryToDelete) {
        try {
            Website existingWebsite = websitesRepository.findByUuid(id);
            if (existingWebsite == null) {
                log.error("Website with id {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Map<String, String> categories = existingWebsite.getCategories();
            categories.remove(categoryToDelete);
            existingWebsite.setCategories(categories);
            websitesRepository.save(existingWebsite);
            log.info("Deleted category: {}", categoryToDelete);
            return ResponseEntity.status(HttpStatus.OK).body(existingWebsite);
        } catch (Exception e) {
            log.error("An error occurred while deleting website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteWebsiteByTitle(String title) {
        try {
            Website website = websitesRepository.findByTitle(title);
            if (website == null) {
                log.error("Article with title {} was not found", title);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Article with title " + title + " was not found");
            } else {
                websitesRepository.delete(website);
                return ResponseEntity.status(HttpStatus.OK).body("Website: " + title + " was successfully deleted");
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
