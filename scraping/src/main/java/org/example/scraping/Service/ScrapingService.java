package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.amqp.RabbitMQMessageProducer;
import org.example.clients.Entities.Article;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.ArticleRepository;
import org.example.scraping.Repositories.WebsitesRepository;
import org.example.scraping.utils.DateUtil;
import org.example.scraping.utils.Scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ScrapingService {

    private final ArticleRepository articleRepository;
    private final WebsitesRepository websitesRepository;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final Scraper scraper;

    public List<Article> scrapeArticles() {
        List<Article> articles = new ArrayList<>();
        Integer articlesToScrape = 1;
        try {
            log.info("Starting fetching process...");
            Map<String, List<String>> allSelectors = scraper.getAllSelectorsForScraping();
            List<String> timeSelectors = allSelectors.get("timeSelectors");

            List<Website> websites = websitesRepository.findAll();

            //Iterate over websites and categories
            for (Website website : websites) {
                String websiteTitle = website.getTitle();
                Map<String, String> categories = website.getCategories();
                log.info("Now fetching website: " + websiteTitle);

                for (Map.Entry<String, String> entry : categories.entrySet()) {
                    String category = entry.getKey();
                    String categoryUrl = entry.getValue();

                    // Fetch article URLs from the current page
                    List<String> articleLinks = scraper.fetchArticlesFromWebsites(allSelectors, categoryUrl);

                    // Scraping articles from fetched URLs
                    for (int i = 0; i < Math.min(articleLinks.size(), articlesToScrape); i++) {
                        String articleTimestamp = fetchArticleTime(Jsoup.connect(articleLinks.get(i)).get(), timeSelectors);
                        if (articleTimestamp != null) {
                            Article articleData = scraper.scrapeArticleContent(articleLinks.get(i), category, articleTimestamp);
                            if (articleData != null) {
                                articles.add(articleData);
                            } else {
                                log.warn("Article was 3 days old or more, skipped");
                            }
                        }
                    }
                }
            }
            log.info("Finished fetching");
        } catch (IOException e) {
            log.error("Error fetching articles. {}", e.getMessage());
        }
        return articles;
    }

    public void saveArticles(List<Article> articles) {
        try {
            List<Article> newArticles = new ArrayList<>();
            Integer newArticlesCounter = 0;

            articles.sort(Comparator.comparing(Article::getTime, Comparator.nullsLast(Comparator.naturalOrder())));
            log.info("Inserting articles to MongoDB...");

            for (Article article : articles) {
                Article existingArticle = articleRepository.findByUrl(article.getUrl());
                if (existingArticle == null) {
                    Article savedArticle = articleRepository.save(article);
                    newArticles.add(savedArticle);
                    newArticlesCounter++;
                }
            }
            for (Article newArticle : newArticles) {
                log.info("New article added: {}", newArticle.getTitle());
            }

            log.info("Articles added: {}", newArticlesCounter);
        } catch (Exception e) {
            log.error("Error occurred while saving articles", e);
        }
    }

    public void deleteOldArticles() {
        try {
            //Calculate 3 days ago
            String threeDaysAgo = String.valueOf(LocalDate.now().minusDays(3));
            log.info("Three days ago it was " + threeDaysAgo);
            log.info("Deleting old articles...");

            //Find old articles
            List<Article> oldArticles = articleRepository.findByTimeBefore(threeDaysAgo);

            //Delete articles older than 3 days
            articleRepository.deleteByTimeBefore(threeDaysAgo);
            log.info(oldArticles.size() + " articles were deleted");
        } catch (Exception e) {
            log.error("Error occurred while deleting old articles", e);
        }
    }

    public ResponseEntity<String> saveToElastic() {
        try {
            List<Article> articles = articleRepository.findAll();
            if(articles.isEmpty()) {
                log.warn("No articles found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No articles were found to save to Elastic");
            }
            log.info("Passing data to elastic");
            rabbitMQMessageProducer.publish(articles, "internal.exchange", "internal.elastic-saver.routing-key");
            return ResponseEntity.status(HttpStatus.OK).body("Save completed");
        } catch (Exception e) {
            log.error("Error occurred while saving articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving articles" + e.getMessage());
        }
    }

    private String fetchArticleTime(Document document, List<String> timeSelectors) {
        String time = null;
        boolean foundTimestamp = false;
        for (String selector : timeSelectors) {
            // Select elements matching the current CSS selector
            Elements elements = document.select(selector);
            if (!elements.isEmpty()) {
                time = elements.get(0).attr("datetime");
                if (!Objects.equals(time, "")) {
                    foundTimestamp = true;
                }
                break;
            }
        }
        if (!foundTimestamp) {
            time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        }
        if (DateUtil.isArticleOlderThanThreeDays(time)) {
            return null;
        }
        return time;
    }
}