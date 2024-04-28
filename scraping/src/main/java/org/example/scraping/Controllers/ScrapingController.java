package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Entities.Website;
import org.example.scraping.Service.ScrapingService;
import org.example.scraping.Service.SelectorService;
import org.example.scraping.Service.WebsiteService;
import org.example.scraping.dto.AddCategoryRequest;
import org.example.scraping.dto.AddWebsiteRequest;
import org.example.scraping.dto.DeleteCategoryRequest;
import org.example.scraping.dto.EditWebsiteRequest;
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

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkScrapingServiceStatus() {
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PreProcessedArticle>> fetchArticles() {
        //Scrape websites
        log.info("Fetching articles");
        List<PreProcessedArticle> fetchedArticles = scrapingService.fetchArticlesFromWebsites();
        scrapingService.savePreProcessedArticles(fetchedArticles);
        scrapingService.getAllPreprocessedArticles();
        return ResponseEntity.status(HttpStatus.OK).body(fetchedArticles);
    }

    @PostMapping("/scrape/elastic")
    private ResponseEntity<String> saveToElastic() {
        return scrapingService.saveToElastic();
    }

    @GetMapping("/website")
    public ResponseEntity<List<Website>> getAllWebsites() {
        return websiteService.getAllWebsites();
    }

    @GetMapping("/website/{uuid}")
    public ResponseEntity<Website> getWebsiteById(@PathVariable String uuid) {
        return websiteService.getWebsiteByUUID(uuid);
    }

    @PostMapping("/website/add")
    public ResponseEntity<Website> saveWebsite(@RequestBody AddWebsiteRequest addWebsiteRequest) {
        String title = addWebsiteRequest.getTitle();
        String icon = addWebsiteRequest.getIcon();
        String value = addWebsiteRequest.getValue();
        return websiteService.saveWebsite(title, icon, value);
    }

    @PutMapping("/website/{uuid}/edit")
    public ResponseEntity<Website> editWebsite(@PathVariable String uuid, @RequestBody EditWebsiteRequest editWebsiteRequest) {
        String title = editWebsiteRequest.getTitle();
        String icon = editWebsiteRequest.getIcon();
        String value = editWebsiteRequest.getValue();
        return websiteService.editWebsite(uuid, title, icon, value);
    }

    @DeleteMapping("/website/{uuid}/delete")
    public ResponseEntity<String> deleteWebsite(@PathVariable String uuid) {
        return websiteService.deleteWebsite(uuid);
    }

    @PostMapping("/website/category/add")
    public ResponseEntity<Website> saveWebsiteCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
        String id = addCategoryRequest.getId();
        String category = addCategoryRequest.getCategory();
        String url = addCategoryRequest.getUrl();
        return websiteService.saveWebsiteCategory(id, category, url);
    }

    @PutMapping("/website/category/delete")
    public ResponseEntity<Website> deleteWebsiteCategory(@RequestBody DeleteCategoryRequest deleteCategoryRequest) {
        String id = deleteCategoryRequest.getId();
        String categoryToDelete = deleteCategoryRequest.getCategoryToDelete();
        return websiteService.deleteWebsiteCategory(id, categoryToDelete);
    }

    @PostMapping("/selector/add/{id}")
    public ResponseEntity<Selector> addSelector(@PathVariable String id, @RequestBody String newSelector) {
        return selectorService.addSelector(id, newSelector);
    }

    @DeleteMapping("/selector/delete/{id}")
    public ResponseEntity<Selector> deleteSelectorByName(@PathVariable String id, @RequestBody String selectorToRemove) {
        return selectorService.removeSelector(id, selectorToRemove);
    }

    @GetMapping("/selectors")
    public ResponseEntity<List<Selector>> getAllSelectors() {
        return selectorService.getAllSelectors();
    }
}