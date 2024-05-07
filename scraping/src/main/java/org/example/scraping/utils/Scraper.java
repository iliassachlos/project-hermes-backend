package org.example.scraping.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.clients.MachineLearningClient;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Repositories.SelectorRepository;
import org.example.scraping.Repositories.WebsitesRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Data
@Slf4j
@Component
public class Scraper {

    private final SelectorRepository selectorRepository;
    private final WebsitesRepository websitesRepository;

    private final MachineLearningClient machineLearningClient;

    public Map<String, List<String>> getAllSelectorsForScraping() {
        Map<String, List<String>> selectorsMap = new HashMap<>();
        List<Selector> allSelectors = selectorRepository.findAll();

        for (Selector selector : allSelectors) {
            selectorsMap.put(selector.getName(), selector.getSelectors());
        }
        return selectorsMap;
    }

    public List<String> fetchArticlesFromWebsites(Map<String, List<String>> allSelectors, String categoryUrl) throws IOException {
        Document document = Jsoup.connect(categoryUrl).get();
        List<String> articleLinks = new ArrayList<>();
        for (String startingSelector : allSelectors.get("startingArticleLinks")) {
            // Select elements matching the starting selector and extract their link
            Elements links = document.select(startingSelector + " a");
            for (Element link : links) {
                articleLinks.add(link.attr("abs:href"));
            }
            if (!articleLinks.isEmpty()) {
                break;
            }
        }
        return articleLinks;
    }

    public PreProcessedArticle scrapeArticleContent(String articleURL, String category, String articleTimestamp) {
        Map<String, List<String>> allSelectors = getAllSelectorsForScraping();

        List<String> titleSelectors = allSelectors.get("titleSelectors");
        List<String> articleSelectors = allSelectors.get("articleSelectors");
        List<String> timeSelectors = allSelectors.get("timeSelectors");

        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        Integer randomViews = randomDataGenerator.nextInt(100, 2000);

        try {
            // Use Jsoup to connect to the article URL and retrieve its HTML document
            Document document = Jsoup.connect(articleURL).get();

            // Fetch title of the article using CSS Selectors
            String articleTitle = fetchArticleTitle(document, titleSelectors);

            // Fetch content of the article
            String articleContent = fetchArticleContent(document, articleSelectors);

            // Fetch timestamp of the article
//            String articleTimestamp = fetchArticleTime(document, timeSelectors);

            // Fetch source of the article
            String articleSource = fetchArticleSource(document, articleURL);

            // Fetch image of the article
            String articleImage = fetchArticleImage(document, articleSource);

            return PreProcessedArticle.builder()
                    .uuid(UUID.randomUUID().toString())
                    .url(articleURL)
                    .title(articleTitle)
                    .content(articleContent)
                    .time(articleTimestamp)
                    .image(articleImage)
                    .source(articleSource)
                    .category(category)
                    .views(randomViews)
                    .build();
        } catch (IOException e) {
            log.error("Error Scraping Articles " + e.getMessage());
            return null;
        }
    }

    private String fetchArticleTitle(Document document, List<String> titleSelectors) {
        String title = null;
        String[] generalTitleSelectors = {"h1, h2, h3"};

        // Fetch title using titleSelectors
        for (String selector : titleSelectors) {
            Elements elements = document.select(selector);
            if (!elements.isEmpty()) {
                title = elements.get(0).text().trim();
                break;
            }
        }

        // If title is still null, use more general selectors
        if (title == null) {
            for (String selector : generalTitleSelectors) {
                Elements elements = document.select(selector);
                if (!elements.isEmpty()) {
                    title = (elements.get(0).text().trim());
                    break;
                }
            }
        }
        return title;
    }

    private String fetchArticleContent(Document document, List<String> articleSelectors) {
        String content = null;

        // Fetch content using articleSelectors
        for (String selector : articleSelectors) {
            Elements elements = document.select(selector);
            if (!elements.isEmpty()) {
                content = elements.get(0).text().trim().replaceAll("<[^>]*>", "");
                break;
            }
        }

        //If content is still null, use a general approach
        if (content == null || content.isEmpty()) {
            Elements dynamicElements = document.body().select("*");
            Element bestBlock = null;
            int maxScore = 0;

            for (Element element : dynamicElements) {
                int score = element.ownText().length();
                if (score > maxScore) {
                    maxScore = score;
                    bestBlock = element;
                }
            }
            if (bestBlock != null) {
                Element parent = bestBlock.parent();
                if (parent != null && parent.hasText()) {
                    content = parent.text();
                } else {
                    content = bestBlock.text();
                }
            }
        }
        return content;
    }

//    private String fetchArticleTime(Document document, List<String> timeSelectors) {
//        String time = null;
//        boolean foundTimestamp = false;
//        for (String selector : timeSelectors) {
//            // Select elements matching the current CSS selector
//            Elements elements = document.select(selector);
//            if (!elements.isEmpty()) {
//                time = elements.get(0).attr("datetime");
//                if (!Objects.equals(time, "")) {
//                    foundTimestamp = true;
//                }
//                break;
//            }
//        }
//        if (!foundTimestamp) {
//            time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
//        }
//
//        if (DateUtil.isArticleOlderThanThreeDays(time)) {
//            return null;
//        }
//
//        return time;
//    }

    private String fetchArticleSource(Document document, String articleURL) {
        Element sourceElement = document.selectFirst("meta[property=og:site_name]");
        String source;

        // If source element is found, set the source of the article; otherwise, set it to "Unknown"
        if (sourceElement == null) {
            source = "Unknown";
        } else {
            source = sourceElement.attr("content").trim();
        }

        //If source length is greater than 25 chars (Possibly wrong case), set source as empty string
        if (source.length() > 25) {
            source = " ";
        }

        //If source is unknown or empty, try to get website name from URL
        if (source.equals("Unknown") || source.equals(" ")) {
            try {
                URI uri = new URI(articleURL);
                String[] hostParts = uri.getHost().split("//([^/]+)");
                if (hostParts.length > 0) {
                    source = hostParts[0];
                } else {
                    source = "Unknown";
                }
            } catch (URISyntaxException e) {
                log.error("URISyntaxException: {}", e.getMessage());
            }
        }
        return source;
    }

    private String fetchArticleImage(Document document, String articleSource) {
        Elements images = document.select("img");
        String image = null;

        for (Element img : images) {
            //Get src and alt attributes of the image
            String src = img.attr("src");
            String alt = img.attr("alt");

            // Check if both 'src' and 'alt' attributes are not empty, and if the image does not contain excluded words
            if (!src.isEmpty() && !alt.isEmpty() && !ImageUtil.containsExcludedWords(src, alt)) {
                if (!src.startsWith("https")) {
                    image = "https://" + articleSource + src;
                } else {
                    image = src;
                }
                break;
            }
        }
        return image;
    }
}

