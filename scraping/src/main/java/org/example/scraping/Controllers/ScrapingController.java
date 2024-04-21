package org.example.scraping.Controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.amqp.RabbitMQMessageProducer;
import org.example.clients.ElasticsearchClient;
import org.example.clients.Entities.PreProcessedArticle;
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

    private final ElasticsearchClient elasticsearchClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @GetMapping("/scrape")
    @ResponseStatus(HttpStatus.OK)
    public List<PreProcessedArticle> fetchArticles() {
        //Scrape websites
        log.info("Fetching articles");
        List<PreProcessedArticle> fetchedArticles = scrapingService.fetchArticlesFromWebsites();

        saveToElastic(fetchedArticles);

        //Save websites to pre-processed-articles document
//        log.info("Saving to pre-processed-articles document");
//        scrapingService.savePreProcessedArticles(fetchedArticles);

        //get websites from pre-processed-articles document (Way to fix microservice bug)
//        log.info("getting websites from pre-processed-articles document");
//        List<PreProcessedArticle> preProcessedArticles = scrapingService.getAllPreprocessedArticles();


//        //save pre-processed-articles to elastic
//        log.info("saving pre-processed-articles to elastic");
//        elasticsearchClient.saveArticles(preProcessedArticles);

        return fetchedArticles;
    }

    private void saveToElastic(List<PreProcessedArticle> articles) {
        log.info("passing data to elastic");
        rabbitMQMessageProducer.publish(articles, "internal.exchange", "internal.elastic-saver.routing-key");
    }

    @PostMapping("/website/add")
    public ResponseEntity<Website> saveWebsite(@RequestBody Website website) {
        return websiteService.saveWebsite(website);
    }

    @DeleteMapping("/website/delete")
    public ResponseEntity<String> deleteWebsite(@RequestBody String title) {
        return websiteService.deleteWebsiteByTitle(title);
    }

    @PostMapping("/website/add/{id}")
    public ResponseEntity<Selector> addSelector(@PathVariable String id, @RequestBody String newSelector) {
        return selectorService.addSelector(id, newSelector);
    }

    @DeleteMapping("/website/delete/{id}")
    public ResponseEntity<Selector> deleteSelectorByName(@PathVariable String id, @RequestBody String selectorToRemove) {
        return selectorService.removeSelector(id, selectorToRemove);
    }
}