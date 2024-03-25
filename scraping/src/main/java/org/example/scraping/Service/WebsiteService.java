package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class WebsiteService {
    private final WebsitesRepository websitesRepository;

    public Map<String, Map<String, String>> getAllWebsiteCategories(){
        List<Website> allWebsites = websitesRepository.findAll();
        return allWebsites.stream().collect(Collectors.toMap(Website::getTitle, Website::getCategories));
    }
}
