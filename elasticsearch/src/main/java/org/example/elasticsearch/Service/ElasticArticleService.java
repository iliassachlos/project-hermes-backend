package org.example.elasticsearch.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.clients.Entities.Article;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Repository.ElasticArticleRepository;
import org.example.elasticsearch.dto.BooleanSearchRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ElasticArticleService {
    private final ElasticArticleRepository elasticArticleRepository;
    private final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));

    public ElasticArticle transformArticleToElasticArticle(Article article) {
        return ElasticArticle.builder()
                .id(article.getId())
                .uuid(article.getUuid())
                .url(article.getUrl())
                .title(article.getTitle())
                .content(article.getContent())
                .image(article.getImage())
                .time(article.getTime())
                .source(article.getSource())
                .category(article.getCategory())
                .views(article.getViews())
                .sentimentScore(article.getSentimentScore())
                .build();
    }

    public void saveArticles(List<Article> articles) {
        List<ElasticArticle> elasticArticles = new ArrayList<>();
        try {
            for (Article article : articles) {
                elasticArticles.add(transformArticleToElasticArticle(article));
            }
            elasticArticleRepository.saveAll(elasticArticles);
            log.info("All Articles were saved to Elasticsearch");
        } catch (Exception e) {
            log.error("An error occurred while saving articles to Elasticsearch", e);
        }
    }

    public SearchResponse dynamicBoolQueryImpl(BooleanSearchRequest searchParams) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // Add must (AND) conditions
        for (Map<String, String> mustParams : searchParams.getMust()) {
            mustParams.forEach((key, value) -> {
                boolQueryBuilder.must(QueryBuilders.termQuery(key, value));
            });
        }

        // Add should (OR) conditions
        for (Map<String, String> shouldParams : searchParams.getShould()) {
            shouldParams.forEach((key, value) -> {
                boolQueryBuilder.should(QueryBuilders.termQuery(key, value));
            });
        }

        // Add must_not (NOT) conditions
        for (Map<String, String> mustNotParams : searchParams.getMust_not()) {
            mustNotParams.forEach((key, value) -> {
                boolQueryBuilder.mustNot(QueryBuilders.termQuery(key, value));
            });
        }

        TermsAggregationBuilder categoryAggregation = AggregationBuilders.terms("category_agg").field("category.keyword");
        TermsAggregationBuilder sourceAggregation = AggregationBuilders.terms("source_agg").field("source.keyword");
        TermsAggregationBuilder sentimentAggregation = AggregationBuilders.terms("sentiment_agg").field("sentimentScore");

        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(categoryAggregation);
        searchSourceBuilder.aggregation(sourceAggregation);
        searchSourceBuilder.aggregation(sentimentAggregation);
        searchSourceBuilder.size(1000);
        searchRequest.source(searchSourceBuilder);

        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public SearchResponse sentimentScoreDistributionQuery(int days) throws IOException {
        // Define the time range query
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("time")
                .gte("now-" + days + "d/d")
                .lte("now/d");

        // Define the sentiment score aggregation
        TermsAggregationBuilder sentimentScoreAgg = AggregationBuilders.terms("sentiment_score_distribution")
                .field("sentimentScore")
                .size(100); // Adjust the size as needed

        // Define the category aggregation
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("category_distribution")
                .field("category.keyword")
                .size(100); // Adjust the size as needed

        TermsAggregationBuilder sourceAgg = AggregationBuilders.terms("source_distribution")
                .field("source.keyword")
                .size(100);

        // Create the search source builder and add the query and aggregations
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(rangeQuery);
        searchSourceBuilder.aggregation(sentimentScoreAgg);
        searchSourceBuilder.aggregation(categoryAgg);
        searchSourceBuilder.aggregation(sourceAgg);
        searchSourceBuilder.size(0); // No need for hits, just aggregations

        // Create the search request and set the source
        SearchRequest searchRequest = new SearchRequest("articles");
        searchRequest.source(searchSourceBuilder);

        // Execute the search request and return the response
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }


}
