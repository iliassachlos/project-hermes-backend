package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class WebsiteService {
    private final WebsitesRepository websitesRepository;

    public Website saveWebsite(Website website) {
        return websitesRepository.save(website);
    }

    public void deleteWebsiteByTitle(String title) {
        Website website = websitesRepository.findByTitle(title);
        if (website != null) {
            websitesRepository.delete(website);
        }
    }
}
