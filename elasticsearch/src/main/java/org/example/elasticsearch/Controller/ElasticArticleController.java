package org.example.elasticsearch.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Service.ElasticArticleService;

import org.example.elasticsearch.dto.BooleanSearchRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/elastic")
@RequiredArgsConstructor
public class ElasticArticleController {

    private final ElasticArticleService elasticArticleService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkElasticServiceStatus() {
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PostMapping("/save")
    public void saveArticles(@RequestBody List<Article> preProcessedArticles) {
        log.info("Inside ELASTIC SAVE ");
        elasticArticleService.saveArticles(preProcessedArticles);
    }

    @PostMapping("/search")
    public Map<String, Object> dynamicSearch(@RequestBody BooleanSearchRequest searchParams) throws IOException {
        SearchResponse searchResponse = elasticArticleService.dynamicBoolQueryImpl(searchParams);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ElasticArticle> articles = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ElasticArticle article = objectMapper.convertValue(sourceAsMap, ElasticArticle.class);
            articles.add(article);
        }

        Terms categories = searchResponse.getAggregations().get("category_agg");
        List<? extends Terms.Bucket> categoryBuckets = categories.getBuckets();

        Terms sources = searchResponse.getAggregations().get("source_agg");
        List<? extends Terms.Bucket> sourceBuckets = sources.getBuckets();

        Terms sentiment = searchResponse.getAggregations().get("sentiment_agg");
        List<? extends Terms.Bucket> sentimentBuckets = sentiment.getBuckets();

        Map<String, Object> response = new HashMap<>();
        response.put("articles", articles);
        response.put("totalHits", searchResponse.getHits().getTotalHits().value); // Total hits metadata
        response.put("maxScore", searchResponse.getHits().getMaxScore()); // Max score metadata
        response.put("categoryFacets", categoryBuckets.stream().collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount)));
        response.put("sourceFacets", sourceBuckets.stream().collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount)));
        response.put("sentimentFacets", sentimentBuckets.stream().collect(Collectors.toMap(Terms.Bucket::getKeyAsNumber, Terms.Bucket::getDocCount)));
        return response;
    }
}
