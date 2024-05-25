package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public ResponseEntity<Website> saveWebsite(String title, String icon, String value) {
        try {
            if (title == null || title.isEmpty() || icon == null || icon.isEmpty() || value == null || value.isEmpty()) {
                log.error("One or more of the required fields are empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Map<String, String> categories = new HashMap<>();
            Website newWebsite = Website.builder()
                    .id(new ObjectId())
                    .uuid(UUID.randomUUID().toString())
                    .title(title)
                    .icon(icon)
                    .value(value)
                    .categories(categories)
                    .build();

            Website savedWebsite = websitesRepository.save(newWebsite);
            log.info("Created a new website with title = {}", newWebsite.getTitle());
            return ResponseEntity.status(HttpStatus.OK).body(savedWebsite);
        } catch (Exception e) {
            log.error("Error occurred while saving website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> editWebsite(String id, String title, String icon, String value) {
        try {
            if (title == null || title.isEmpty() || icon == null || icon.isEmpty() || value == null || value.isEmpty()) {
                log.error("One or more of required fields is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Website existingWebsite = websitesRepository.findByUuid(id);
            if (existingWebsite == null) {
                log.error("Website with id {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            existingWebsite.setTitle(title);
            existingWebsite.setIcon(icon);
            existingWebsite.setValue(value);
            Website savedWebsite = websitesRepository.save(existingWebsite);
            log.info("Saved website with id: {}", savedWebsite);
            return ResponseEntity.status(HttpStatus.OK).body(savedWebsite);
        } catch (Exception e) {
            log.error("An error occurred while editing website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteWebsite(String id) {
        try {
            Website website = websitesRepository.findByUuid(id);
            if (website == null) {
                log.error("Website with title {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Website with title " + id + " was not found");
            }
            websitesRepository.delete(website);
            return ResponseEntity.status(HttpStatus.OK).body("Website: " + id + " was successfully deleted");
        } catch (Exception e) {
            log.error("Error occurred while deleting website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Website> saveWebsiteCategory(String id, String category, String url) {
        try {
            if (id == null || id.isEmpty() || category == null || category.isEmpty() || url == null || url.isEmpty()) {
                log.error("One or more of required fields are empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
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
            if (id == null || id.isEmpty() || categoryToDelete == null || categoryToDelete.isEmpty()) {
                log.error("One or more of required fields are empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
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
}
