package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class WebsiteService {
    private final WebsitesRepository websitesRepository;

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

    public ResponseEntity<String> deleteWebsiteByTitle(String title) {
        try{
            Website website = websitesRepository.findByTitle(title);
            if (website == null) {
                log.error("Article with title {} was not found",title);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Article with title " + title + " was not found");
            } else {
                websitesRepository.delete(website);
                return ResponseEntity.status(HttpStatus.OK).body("Website: " + title + " was successfully deleted");
            }
        }catch (Exception e){
            log.error("Error occurred while deleting website", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
