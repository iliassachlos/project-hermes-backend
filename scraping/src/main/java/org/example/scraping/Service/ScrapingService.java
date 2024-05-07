package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.amqp.RabbitMQMessageProducer;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.clients.MachineLearningClient;
import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.ArticleRepository;
import org.example.scraping.Repositories.PreProcessedArticleRepository;
import org.example.scraping.Repositories.WebsitesRepository;
import org.example.scraping.utils.DateUtil;
import org.example.scraping.utils.Scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    private final PreProcessedArticleRepository preProcessedArticleRepository;
    private final ArticleRepository articleRepository;
    private final WebsitesRepository websitesRepository;

    private final MachineLearningClient machineLearningClient;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final RabbitTemplate rabbitTemplate;
    private final Scraper scraper;

    public List<PreProcessedArticle> scrapeArticles() {
        List<PreProcessedArticle> articles = new ArrayList<>();
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
                            PreProcessedArticle articleData = scraper.scrapeArticleContent(articleLinks.get(i), category, articleTimestamp);
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

    public List<PreProcessedArticle> getAllPreprocessedArticles() {
        try {
            return preProcessedArticleRepository.findAll();
        } catch (Exception e) {
            log.error("An error occurred while trying to get all articles");
            return null;
        }
    }

    public void savePreProcessedArticles(List<PreProcessedArticle> articles) {
        List<PreProcessedArticle> newArticles = new ArrayList<>();
        Integer newArticlesCounter = 0;

        articles.sort(Comparator.comparing(PreProcessedArticle::getTime, Comparator.nullsLast(Comparator.naturalOrder())));

        log.info("Inserting articles to MongoDB...");
        try {
            for (PreProcessedArticle article : articles) {
                // Check if article already exists in MongoDB based on URL
                PreProcessedArticle existingArticle = preProcessedArticleRepository.findByUrl(article.getUrl());
                if (existingArticle == null) {
                    //IF article not exist, save it to MongoDB
                    PreProcessedArticle savedArticle = preProcessedArticleRepository.save(article);
                    newArticles.add(savedArticle);
                    newArticlesCounter++;
                }
            }
            for (PreProcessedArticle newArticle : newArticles) {
                log.info("New article added: {}", newArticle.getTitle());
            }
            log.info("Articles added: {}", newArticlesCounter);
        } catch (Exception e) {
            log.error("Error occurred while saving articles", e);
        }
    }

    public void deleteOldPreProcessedArticles() {
        //Calculate 3 days ago
        String threeDaysAgo = String.valueOf(LocalDate.now().minusDays(3));
        log.info("Three days ago it was " + threeDaysAgo);
        log.info("Deleting old articles...");
        try {
            //Find old articles
            List<PreProcessedArticle> oldArticles = preProcessedArticleRepository.findByTimeBefore(threeDaysAgo);
            //Delete articles older than 3 days
            preProcessedArticleRepository.deleteByTimeBefore(threeDaysAgo);
            log.info(oldArticles.size() + " articles were deleted");
        } catch (Exception e) {
            log.error("Error occurred while deleting old articles", e);
        }
    }

    public void saveArticles(List<Article> articles) {
        List<Article> newArticles = new ArrayList<>();
        Integer newArticlesCounter = 0;

        articles.sort(Comparator.comparing(Article::getTime, Comparator.nullsLast(Comparator.naturalOrder())));

        log.info("Inserting articles to MongoDB...");
        try {
            for (Article article : articles) {
                // Check if article already exists in MongoDB based on URL
                Article existingArticle = articleRepository.findByUrl(article.getUrl());
                if (existingArticle == null) {
                    //IF article not exist, save it to MongoDB
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
        //Calculate 3 days ago
        String threeDaysAgo = String.valueOf(LocalDate.now().minusDays(3));
        log.info("Three days ago it was " + threeDaysAgo);
        log.info("Deleting old articles...");
        try {
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
            List<PreProcessedArticle> articles = preProcessedArticleRepository.findAll();
            log.info("Passing data to elastic");
            //elasticsearchClient.saveArticles(preProcessedArticles);

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

    public List<Article> performMachineLearning() {
        log.info("Before performMachineLearning try-catch");
        try {
            List<PreProcessedArticle> articlesForProcess = preProcessedArticleRepository.findAll();
            log.info("Fetched pre-processed articles: " + articlesForProcess.size());
            List<Article> articles = new ArrayList<>();

            for (PreProcessedArticle preProcessedArticle : articlesForProcess) {
                String content = preProcessedArticle.getContent();


                //Double sentiment = machineLearningClient.sentimentAnalysis(content);
                Article processedArticle = Article.builder()
                        .id(preProcessedArticle.getId())
                        .uuid(preProcessedArticle.getUuid())
                        .url(preProcessedArticle.getUrl())
                        .title(preProcessedArticle.getTitle())
                        .content(preProcessedArticle.getContent())
                        .time(preProcessedArticle.getTime())
                        .image(preProcessedArticle.getImage())
                        .source(preProcessedArticle.getSource())
                        .category(preProcessedArticle.getCategory())
                        .views(preProcessedArticle.getViews())
                        .sentiment(sentiment)
                        .build();
                articles.add(processedArticle);
            }
            log.info("Performed machine-learning");
            articleRepository.saveAll(articles);
            log.info("Articles saved: " + articles.size());
            return articles;
        } catch (Exception e) {
            log.error("Error occurred while performing machine learning", e);
            return null;
        }
    }
}