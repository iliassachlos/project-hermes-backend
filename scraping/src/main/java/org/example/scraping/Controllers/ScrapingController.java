package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.Article;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Entities.Website;
import org.example.scraping.Service.ScrapingService;
import org.example.scraping.Service.SelectorService;
import org.example.scraping.Service.WebsiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/scraping")
@RequiredArgsConstructor
public class ScrapingController {

    private final ScrapingService scrapingService;
    private final WebsiteService websiteService;
    private final SelectorService selectorService;

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public List<Article> fetchArticles() {
        List<Article> articles = scrapingService.fetchArticlesFromWebsites();
        scrapingService.saveArticles(articles);
        scrapingService.deleteOldArticles();
        return articles;
    }

    @PostMapping("/addWebsite")
    public ResponseEntity<Website> saveWebsite(@RequestBody Website website) {
        Website savedWebsite = websiteService.saveWebsite(website);
        log.info("Created a new website with title = {}", website.getTitle());
        return ResponseEntity.ok(savedWebsite);
    }

    @DeleteMapping("/deleteWebsite")
    public ResponseEntity<Void> deleteWebsite(@RequestBody String title) {
        websiteService.deleteWebsiteByTitle(title);
        log.info("Deleted the website with title = {}", title);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addSelector/{id}")
    public ResponseEntity<Selector> addSelector(@PathVariable String id, @RequestBody String newSelector) {
        Selector updatedSelector = selectorService.addSelector(id, newSelector);
        if (updatedSelector == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Added selector {} in Selectors document with id {}", newSelector, id);
        return ResponseEntity.ok(updatedSelector);
    }

    @DeleteMapping("/deleteSelector/{id}")
    public ResponseEntity<Void> deleteSelectorByName(@PathVariable String id, @RequestBody String selectorToRemove) {
        Selector updatedSelector = selectorService.removeSelector(id, selectorToRemove);
        if (updatedSelector == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Deleted selector {} in Selectors document with id {}", selectorToRemove, id);
        return ResponseEntity.noContent().build();
    }
}