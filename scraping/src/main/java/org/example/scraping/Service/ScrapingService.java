package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.example.scraping.Entities.Article;
import org.example.scraping.Repositories.ArticleRepository;
import org.example.scraping.dto.WebsitesDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ScrapingService {
    private final ArticleRepository articleRepository;

    private final List<String> startingArticleLinks = List.of("h2", "h3", ".media", ".gtr", "div[itemprop=itemListElement]");
    private final List<String> titleSelectors = List.of("h1.fw-headline", ".entry-title", ".article-title", ".article__title", ".headline", ".itemTitle", ".title", "h1", "h2", "h3");
    private final List<String> articleSelectors = List.of("div.articletext", "div.article", "div.entry-content", "div.content", "div.itemFullText", "div.main-text", "div.main-content", "div.story-fulltext", "div.td-post-content", "div.article__body", ".cntTxt", "div.single-article");
    private final List<String> timeSelectors = List.of(".time", ".date", "time");
    public List<Article> fetchArticlesFromWebsites() {
        List<Article> articles = new ArrayList<>();

        try {
            log.info("STARTING FETCHING PROCESS...");

            //Iterate over websites and categories
            for (String website: WebsitesDTO.websites.keySet()){
                log.info("NOW FETCHING WEBPAGE : " + website);

                //Iterate over the categories of each website
                for (String category: WebsitesDTO.websites.get(website).keySet()){
                    //Get URL for the current category
                    String categoryUrl = WebsitesDTO.websites.get(website).get(category);
                    //Use Jsoup to connect to the webpage and retrieve its HTML Document
                    Document document = Jsoup.connect(categoryUrl).get();
                    List<String> articleLinks = new ArrayList<>();

                    //Fetch article urls from the current page
                    for (String startingSelector: startingArticleLinks){
                        //Select elements matching the starting selector and extract their link
                        Elements links = document.select(startingSelector + " a");
                        for (Element link: links){
                            //Add the absolute URL of each link to the articlelinks list
                            articleLinks.add(link.attr("abs:href"));
                        }
                        if(!articleLinks.isEmpty()){
                            break;
                        }
                    }
                    // Scraping articles from fetched URLs
                    for (int i = 0; i< Math.min(articleLinks.size(), 5); i++){
                        // Call the scrapeArticle method to extract the article data from each URL
                        Article articleData = scrapeArticle(articleLinks.get(i), category);
                        articles.add(articleData);
                    }
                }
            }
            log.info("FINISHED FETCHING");
        } catch (IOException e){
            log.error("Error fetching articles. " + e.getMessage());
        }
        return articles;
    }

    private Article scrapeArticle(String articleURL, String category){
        Article articleData = new Article();
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

        //Set the URL, category and views of the article;
        articleData.setUrl(articleURL);
        articleData.setCategory(category);
        articleData.setViews(randomDataGenerator.nextInt(100, 2000));

        try {
            // Use Jsoup to connect to the article URL and retrieve its HTML document
            Document document = Jsoup.connect(articleURL).get();

            // Fetch title of the article using CSS Selectors
            for (String selector : titleSelectors) {
                Elements elements = document.select(selector);
                if (!elements.isEmpty()) {
                    articleData.setTitle(elements.get(0).text().trim());
                    break;
                }
            }

            // Fetch content of the article
            for (String selector : articleSelectors) {
                //Select elements matching the current CSS selector
                Elements elements = document.select(selector);
                if (!elements.isEmpty()) {
                    articleData.setContent(elements.get(0).text().trim().replaceAll("<[^>]*>", ""));
                    break;
                }
            }

            // Fetch timestamp of the article
            boolean foundTimestamp = false;
            for (String selector : timeSelectors) {
                // Select elements matching the current CSS selector
                Elements elements = document.select(selector);
                if (!elements.isEmpty()) {
                    articleData.setTime(elements.get(0).attr("datetime"));
                    foundTimestamp = true;
                    break;
                }
            }
            if(!foundTimestamp){
                String formattedCurrentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
                articleData.setTime(formattedCurrentTime);
            }

            // Fetch source of the article
            Element sourceElement = document.selectFirst("meta[property=og:site_name]");
            String source;
            // If a source element is found, set the source of the article; otherwise, set it to "Unknown"
            if (sourceElement != null) {
                source = sourceElement.attr("content").trim();
            } else {
                source = "Unknown";
            }
            if(source.length() > 25){
                source = " ";
            }
            if(source.equals("Unknown") || source.equals(" ")){
                try {
                    URI uri = new URI(articleURL);
                    String[] hostParts = uri.getHost().split("//([^/]+)");
                    if (hostParts.length > 0) {
                        source = hostParts[0];
                    } else {
                        source = "Unknown";
                    }
                } catch (URISyntaxException e){
                    log.error("URISyntaxException: {}", e.getMessage());
                }
            }
            articleData.setSource(source);

            // Fetch image of the article
            // Select all 'img' elements from the document
            Elements images = document.select("img");
            // Iterate through each image element
            for (Element img : images) {
                //Get src and alt attributes of the image
                String src = img.attr("src");
                String alt = img.attr("alt");
                // Check if both 'src' and 'alt' attributes are not empty, and if the image does not contain excluded words
                if (!src.isEmpty() && !alt.isEmpty() && !containsExcludedWords(src, alt)) {
                    articleData.setImage(src);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error Scraping Articles " + e.getMessage());
        }
        return articleData;
    }

    private boolean containsExcludedWords(String src, String alt) {
        // Define a list of words that are excluded from consideration
        List<String> excludedWords = List.of("logo", "svg", "avatar", "profile", "profiles", "webp", "gif");
        // Iterate over each word in the list of excluded words
        for (String word : excludedWords) {
            // Check if the source URL or the alt text contains the current word
            if (src.contains(word) || alt.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public void saveArticles(List<Article> articles) {
        List<Article> newArticles = new ArrayList<>();
        Integer newArticlesCounter = 0;

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
            log.info(oldArticles.size() + " articles where deleted");
        } catch (Exception e) {
            log.error("Error occurred while deleting old articles", e);
        }
    }
}
