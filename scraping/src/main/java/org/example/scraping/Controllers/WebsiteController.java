package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Website;
import org.example.scraping.Service.WebsiteService;
import org.example.scraping.dto.AddCategoryRequest;
import org.example.scraping.dto.AddWebsiteRequest;
import org.example.scraping.dto.DeleteCategoryRequest;
import org.example.scraping.dto.EditWebsiteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/scraping/website")
@RequiredArgsConstructor
public class WebsiteController {

    private final WebsiteService websiteService;

    @GetMapping("/all")
    public ResponseEntity<List<Website>> getAllWebsites() {
        return websiteService.getAllWebsites();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Website> getWebsiteById(@PathVariable String uuid) {
        return websiteService.getWebsiteByUUID(uuid);
    }

    @PostMapping("/add")
    public ResponseEntity<Website> saveWebsite(@RequestBody AddWebsiteRequest addWebsiteRequest) {
        String title = addWebsiteRequest.getTitle();
        String icon = addWebsiteRequest.getIcon();
        String value = addWebsiteRequest.getValue();
        return websiteService.saveWebsite(title, icon, value);
    }

    @PutMapping("/{uuid}/edit")
    public ResponseEntity<Website> editWebsite(@PathVariable String uuid, @RequestBody EditWebsiteRequest editWebsiteRequest) {
        String title = editWebsiteRequest.getTitle();
        String icon = editWebsiteRequest.getIcon();
        String value = editWebsiteRequest.getValue();
        return websiteService.editWebsite(uuid, title, icon, value);
    }

    @DeleteMapping("/{uuid}/delete")
    public ResponseEntity<String> deleteWebsite(@PathVariable String uuid) {
        return websiteService.deleteWebsite(uuid);
    }

    @PostMapping("/category/add")
    public ResponseEntity<Website> saveWebsiteCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
        String id = addCategoryRequest.getId();
        String category = addCategoryRequest.getCategory();
        String url = addCategoryRequest.getUrl();
        return websiteService.saveWebsiteCategory(id, category, url);
    }

    @PutMapping("/category/delete")
    public ResponseEntity<Website> deleteWebsiteCategory(@RequestBody DeleteCategoryRequest deleteCategoryRequest) {
        String id = deleteCategoryRequest.getId();
        String categoryToDelete = deleteCategoryRequest.getCategoryToDelete();
        return websiteService.deleteWebsiteCategory(id, categoryToDelete);
    }
}
